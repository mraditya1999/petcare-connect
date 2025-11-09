import { useCallback, useEffect, useState } from "react";
import { customFetch } from "@/utils/customFetch";
import { Input } from "@/components/ui/input";
import { Search } from "lucide-react";
import { Button } from "@/components/ui/button";
import "react-quill/dist/quill.snow.css";
import { IFeaturedForumResponse, IForum } from "@/types/forum-types";
import "react-quill/dist/quill.snow.css";
import forumHeaderImg from "@/assets/images/forumpage/forumheader.png";
import { getUserFromStorage } from "@/utils/helpers";
import Categories from "@/components/forum/Categories";
import ForumSection from "@/components/forum/ForumSection";
import ForumEditor from "@/components/forum/ForumEditor";
import SolvedTopics from "@/components/forum/SolvedTopics";
import PaginationControl from "@/components/forum/PaginationControl";
import { IPageResponse } from "@/types/pagination-types";
const ForumPage = () => {
  const [forums, setForums] = useState<IForum[]>([]);
  const [featuredForums, setFeaturedForums] = useState<IForum[]>([]);

  const [newForumTitle, setNewForumTitle] = useState("");
  const [newForumTags, setNewForumTags] = useState<string[]>(["community"]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [newForumContent, setNewForumContent] = useState("");
  const [totalElements, setTotalElements] = useState<number>(0);
  const [page, setPage] = useState<number>(0);
  const [totalPages, setTotalPages] = useState<number>(1);
  const [sortBy, setSortBy] = useState<string>("createdAt");
  const [sortDir, setSortDir] = useState<"asc" | "desc">("desc");
  const [searchTerm, setSearchTerm] = useState<string>("");

  const user = getUserFromStorage();

  const fetchForums = useCallback(async () => {
    setLoading(true);
    try {
      const url = searchTerm.trim()
        ? `/forums/search?keyword=${encodeURIComponent(searchTerm)}&page=${page}&size=5&sortBy=${sortBy}&sortDir=${sortDir}`
        : `/forums?page=${page}&size=5&sortBy=${sortBy}&sortDir=${sortDir}`;

      const forumsResponse = await customFetch.get<IPageResponse<IForum>>(url);
      const forumData = forumsResponse.data;

      setForums(forumData.content);
      setTotalPages(forumData.page.totalPages);
      setTotalElements(forumData.page.totalElements);

      const featuredResponse = await customFetch.get<IFeaturedForumResponse>(
        "forums/top-featured",
      );
      setFeaturedForums(featuredResponse.data.data);
    } catch (err) {
      console.error(err);
      setError("Error fetching forums. Please try again.");
    } finally {
      setLoading(false);
    }
  }, [page, sortBy, sortDir, searchTerm]);

  useEffect(() => {
    const delay = setTimeout(() => fetchForums(), 500);
    return () => clearTimeout(delay);
  }, [fetchForums]);

  // const fetchForums = async () => {
  //   try {
  //     let forumData: IForum[] = [];

  //     if (searchTerm.trim()) {
  //       // 🔍 Call search API when user is typing
  //       const response = await customFetch.get<IForum[]>(
  //         `/forums/search?keyword=${encodeURIComponent(searchTerm)}`,
  //       );
  //       forumData = response.data;
  //       setForums(forumData);
  //       setTotalPages(1); // since search results likely aren’t paginated
  //     } else {
  //       // 📄 Normal paginated fetch
  //       const forumsResponse = await customFetch.get<IPageResponse<IForum>>(
  //         `/forums?page=${page}&size=5&sortBy=${sortBy}&sortDir=${sortDir}`,
  //       );

  //       forumData = forumsResponse.data.content;
  //       setForums(forumData);
  //       setTotalPages(forumsResponse.data.page.totalPages);
  //     }

  //     // 🏆 Fetch featured forums always
  //     const featuredForumsResponse =
  //       await customFetch.get<IFeaturedForumResponse>("forums/top-featured");

  //     setFeaturedForums(featuredForumsResponse.data.data);
  //   } catch (error) {
  //     console.error(error);
  //     setError("Error fetching forums. Please try again.");
  //   } finally {
  //     setLoading(false);
  //   }
  // };

  const handleSearch = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchTerm(e.target.value);
    setPage(0);
  };

  const handleCreateForum = async () => {
    if (newForumContent.trim() === "") return;

    try {
      await customFetch.post("/forums", {
        title: newForumTitle,
        content: newForumContent,
        tags: newForumTags,
      });
      setNewForumContent("");
      setNewForumTitle("New Forum");
      setNewForumTags([]);

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
                // placeholder="Search everything pet care related..."
                placeholder="Search by 'title' or 'description'"
                className="border-0 bg-transparent px-1 focus-visible:ring-0 focus-visible:ring-offset-0"
                value={searchTerm}
                onChange={handleSearch}
              />
            </div>
            {searchTerm && (
              <p className="mt-2 text-sm text-gray-500">
                Found {totalElements} results for "{searchTerm}"
              </p>
            )}
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
          <article className="col-span-2">
            <ForumSection
              title="Recent activity"
              forums={forums}
              loading={loading}
              error={error}
              sortBy={sortBy}
              sortDir={sortDir}
              onSortByChange={setSortBy}
              onSortDirChange={setSortDir}
            />

            <PaginationControl
              currentPage={page}
              totalPages={totalPages}
              onPageChange={setPage}
            />
          </article>

          {/* Solved Topics */}
          <article className="col-span-1">
            <h2 className="mb-4 text-xl font-semibold">Solved topics</h2>
            <SolvedTopics />
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
              className="mb-4"
            />

            {/* Tags Input (Basic Example - Improve as needed) */}
            <Input
              placeholder="Tags (comma-separated)"
              value={newForumTags.join(",")} // Display tags as comma-separated
              onChange={(e) => {
                const tags = e.target.value.split(",").map((tag) => tag.trim());
                setNewForumTags(tags);
              }}
              className="mb-4"
            />

            <ForumEditor
              value={newForumContent}
              onChange={setNewForumContent}
            />

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
