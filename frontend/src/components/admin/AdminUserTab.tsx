import { useState, useEffect } from "react";
import { customFetch } from "@/utils/customFetch";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";

interface User {
  userId: string;
  firstName: string;
  lastName: string;
  email: string;
  verified: boolean;
}

const AdminUserTab = () => {
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const loadUsers = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await customFetch.get("/admin/users?page=0&size=100");
      setUsers(response.data.data?.content || []);
    } catch (err) {
      setError("Failed to load users");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const deleteUser = async (userId: string) => {
    if (!window.confirm("Are you sure you want to delete this user?")) return;
    try {
      await customFetch.delete(`/admin/users/${userId}`);
      setUsers((prev) => prev.filter((u) => u.userId !== userId));
    } catch (err) {
      setError("Failed to delete user");
      console.error(err);
    }
  };

  useEffect(() => {
    loadUsers();
  }, []);

  return (
    <Card className="mt-4">
      <CardHeader className="flex flex-row items-center justify-between">
        <CardTitle>Manage Users</CardTitle>
        <Button onClick={loadUsers} variant="outline">
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
        ) : users.length === 0 ? (
          <div className="text-center text-muted-foreground">
            No users found
          </div>
        ) : (
          <div className="space-y-3">
            {users.map((user) => (
              <div
                key={user.userId}
                className="flex items-center justify-between rounded border border-gray-200 p-3 dark:border-gray-700"
              >
                <div>
                  <p className="font-semibold">
                    {user.firstName} {user.lastName}
                  </p>
                  <p className="text-sm text-muted-foreground">{user.email}</p>
                  <p className="text-xs text-gray-500">
                    {user.verified ? "✓ Verified" : "Not Verified"}
                  </p>
                </div>
                <Button
                  variant="destructive"
                  size="sm"
                  onClick={() => deleteUser(user.userId)}
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

export default AdminUserTab;
