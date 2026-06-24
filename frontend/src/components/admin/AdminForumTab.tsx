import { useState, useEffect } from "react";
import { customFetch } from "@/utils/customFetch";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";

interface Forum {
  forumId: string;
  title: string;
  description: string;
  createdBy?: string;
}

const AdminForumTab = () => {
  const [forums, setForums] = useState<Forum[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const loadForums = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await customFetch.get("/forums?page=0&size=100");
      setForums(response.data.data?.content || []);
    } catch (err) {
      setError("Failed to load forums");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const deleteForum = async (forumId: string) => {
    if (!window.confirm("Are you sure you want to delete this forum?")) return;
    try {
      await customFetch.delete(`/admin/forums/${forumId}`);
      setForums((prev) => prev.filter((f) => f.forumId !== forumId));
    } catch (err) {
      setError("Failed to delete forum");
      console.error(err);
    }
  };

  useEffect(() => {
    loadForums();
  }, []);

  return (
    <Card className="mt-4">
      <CardHeader className="flex flex-row items-center justify-between">
        <CardTitle>Manage Forums</CardTitle>
        <Button onClick={loadForums} variant="outline">
          Refresh
        </Button>
      </CardHeader>
      <CardContent>
        {error && (
          <div className="mb-4 rounded-lg border border-red-300 bg-red-50 p-3 text-red-700">
            {error}
          </div>
        )}
        {loading ? (
          <div>Loading...</div>
        ) : forums.length === 0 ? (
          <div className="text-center text-muted-foreground">
            No forums found
          </div>
        ) : (
          <div className="space-y-3">
            {forums.map((forum) => (
              <div
                key={forum.forumId}
                className="flex items-center justify-between rounded border border-gray-200 p-3 dark:border-gray-700"
              >
                <div>
                  <p className="font-semibold">{forum.title}</p>
                  <p className="text-sm text-muted-foreground">
                    {forum.description}
                  </p>
                </div>
                <Button
                  variant="destructive"
                  size="sm"
                  onClick={() => deleteForum(forum.forumId)}
                >
                  Delete
                </Button>
              </div>
            ))}
          </div>
        )}
      </CardContent>
    </Card>
  );
};

export default AdminForumTab;
