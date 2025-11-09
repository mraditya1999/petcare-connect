import { useEffect, useState } from "react";
import { customFetch } from "@/utils/customFetch";
import { Input } from "@/components/ui/input";
import { Card } from "@/components/ui/card";
import { Search, Check } from "lucide-react";
import { Button } from "@/components/ui/button";
import ReactQuill from "react-quill";
import "react-quill/dist/quill.snow.css";
import { IForum } from "@/types/forum-types";
import "react-quill/dist/quill.snow.css";
import forumHeaderImg from "@/assets/images/forumpage/forumheader.png";
import { getUserFromStorage } from "@/utils/helpers";
import Categories from "@/components/forum/Categories";
import ForumSection from "@/components/forum/ForumSection";

interface IPageResponse<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number; // current page
}
const ForumPage = () => {
  const [newForumTitle, setNewForumTitle] = useState("");
  const [newForumTags, setNewForumTags] = useState<string[]>(["community"]);
  const [forums, setForums] = useState<IForum[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [newForumContent, setNewForumContent] = useState("");
  const [featuredForums, setFeaturedForums] = useState<IForum[]>([]);

  const [page, setPage] = useState<number>(0);
  const [totalPages, setTotalPages] = useState<number>(1);
  const [sortBy, setSortBy] = useState<string>("createdAt");
  const [sortDir, setSortDir] = useState<"asc" | "desc">("desc");
  const [searchTerm, setSearchTerm] = useState<string>("");

  const user = getUserFromStorage();

  const solvedTopics = [
    "Free consultation does my puppy need",
    "How can I socialize my rescue pet with other animals?",
    "What are the best local parks for dog walking?",
    "What are some fun indoor activities for dogs?",
    "What are some engaging toys for cats that keep them engaged?",
    "Natural remedies for itching",
  ];

  const fetchForums = async () => {
    try {
      const forumsResponse = await customFetch.get<IPageResponse<IForum>>(
        `/forums?page=${page}&size=5&sortBy=${sortBy}&sortDir=${sortDir}${
          searchTerm ? `&search=${searchTerm}` : ""
        }`,
      );

      const featuredForumsResponse = await customFetch<IForum>(
        "forums/top-featured",
      );
      const forumData = forumsResponse.data?.content || [];
      setForums(forumData);
      setFeaturedForums(featuredForumsResponse?.data?.data);
    } catch (error) {
      setError("Error fetching forums. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchForums();
  }, []);

  const handleCreateForum = async () => {
    if (newForumContent.trim() === "") return;

    try {
      await customFetch.post("/forums", {
        title: newForumTitle,
        content: newForumContent,
        tags: newForumTags,
      });
      setNewForumContent("");
      setNewForumTitle("New Forum"); // Reset title
      setNewForumTags([]); // Reset tags
      fetchForums();
    } catch (error) {
      console.error("Error creating forum:", error);
    }
  };

  return (
    <div className="min-h-screen">
      {/* Header */}
      <section
        className="flex h-96 max-h-[30rem] w-full items-center justify-center pt-16"
        style={{
          backgroundImage: `url('${forumHeaderImg}')`,
          backgroundPosition: "center",
          backgroundSize: "cover",
        }}
      >
        <div className="max-w-6xl px-4">
          <h1 className="mb-4 text-center text-4xl font-bold sm:text-6xl">
            Pet Care Connect
          </h1>
          <p className="tight mb-8 text-center text-xs text-gray-600 md:text-sm">
            Share, Learn, and Connect with Fellow Pet Owners
          </p>

          {/* Search Bar */}
          <div className="max-w-3xl">
            <div className="flex items-center gap-1 rounded-md bg-white p-2">
              <Search className="h-5 w-5 text-gray-400" />
              <Input
                placeholder="Search everything pet care related..."
                className="border-0 bg-transparent px-1 focus-visible:ring-0 focus-visible:ring-offset-0"
              />
            </div>
          </div>
        </div>
      </section>

      {/* Categories */}
      <section className="py-16">
        <Categories />
      </section>

      {/* Featured Topics (Now Featured Forums) */}
      <section className="section-width py-16">
        <ForumSection
          title="Featured Forums"
          forums={featuredForums}
          loading={loading}
          error={error}
          emptyMessage="No featured forums available yet."
        />
      </section>

      {/* Main Forums + Solved Topics */}
      <section className="py-16">
        <div className="section-width grid gap-8 lg:col-span-2 lg:grid-cols-3">
          {/* Display Forums */}
          <article className="col-span-2">
            <ForumSection
              title="Recent activity"
              forums={forums}
              loading={loading}
              error={error}
            />
          </article>

          {/* Solved Topics */}
          <article className="col-span-1">
            <h2 className="mb-4 text-xl font-semibold">Solved topics</h2>
            <div className="space-y-3">
              {solvedTopics.map((topic, index) => (
                <Card
                  key={index}
                  className="cursor-pointer border-0 bg-white p-4"
                >
                  <div className="flex items-start gap-2">
                    <Check className="mt-1 h-4 w-4 flex-shrink-0 text-green-500" />
                    <p className="text-sm">{topic}</p>
                  </div>
                </Card>
              ))}
            </div>
          </article>
        </div>
      </section>

      {/* Create Forum Section with Input Fields */}
      {user && (
        <section className="py-16">
          <div className="section-width mx-auto px-6">
            <h2 className="mb-4 text-xl font-semibold">Create a New Forum</h2>

            {/* Title Input */}
            <Input
              placeholder="Forum Title"
              value={newForumTitle}
              onChange={(e) => setNewForumTitle(e.target.value)}
              className="mb-4" // Add some margin below
            />

            {/* Tags Input (Basic Example - Improve as needed) */}
            <Input
              placeholder="Tags (comma-separated)"
              value={newForumTags.join(",")} // Display tags as comma-separated
              onChange={(e) => {
                const tags = e.target.value.split(",").map((tag) => tag.trim());
                setNewForumTags(tags);
              }}
              className="mb-4" // Add some margin below
            />

            <div className="mb-4 min-h-[13rem]">
              <ReactQuill
                className="h-full"
                value={newForumContent}
                onChange={setNewForumContent}
                modules={{
                  toolbar: [
                    ["bold", "italic"],
                    [{ list: "ordered" }, { list: "bullet" }],
                    ["link"],
                  ],
                }}
                formats={["bold", "italic", "list", "bullet", "link"]}
                placeholder="Write your forum content here..."
              />
            </div>
            <Button onClick={handleCreateForum} className="mt-4">
              {loading ? "Creating..." : "Create Forum"}
            </Button>
            {error && <p className="mt-2 text-red-500">{error}</p>}
          </div>
        </section>
      )}
    </div>
  );
};

export default ForumPage;
