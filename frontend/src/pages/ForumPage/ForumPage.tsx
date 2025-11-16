import React, { useCallback, useEffect, useState } from "react";
import { Input } from "@/components/ui/input";
import { Search } from "lucide-react";
import { Button } from "@/components/ui/button";
import "react-quill/dist/quill.snow.css";
import forumHeaderImg from "@/assets/images/forumpage/forumheader.png";
import { getUserFromStorage, showToast } from "@/utils/helpers";
import Categories from "@/components/forum/Categories";
import ForumSection from "@/components/forum/ForumSection";
import ForumEditor from "@/components/forum/ForumEditor";
import SolvedTopics from "@/components/forum/SolvedTopics";
import PaginationControl from "@/components/forum/PaginationControl";
import { useAppDispatch, useAppSelector } from "@/app/hooks";
import { useDebounce } from "@/hooks/useDebounce";
import {
  fetchForums,
  fetchFeaturedForums,
  createForum,
} from "@/features/forumList/forumListThunk";
import {
  setPage,
  setSortBy,
  setSortDir,
  setSearchTerm,
  setTagSearchTerm,
} from "@/features/forumList/forumListSlice";

const ForumPage: React.FC = () => {
  const dispatch = useAppDispatch();

  const {
    forums,
    featuredForums,
    loading,
    error,
    page,
    size,
    totalPages,
    totalElements,
    sortBy,
    sortDir,
    searchTerm,
    tagSearchTerm,
  } = useAppSelector((state) => state.forumList);

  const [newForumTitle, setNewForumTitle] = useState("");
  const [newForumTags, setNewForumTags] = useState<string[]>(["community"]);
  const [newForumContent, setNewForumContent] = useState("");

  const user = getUserFromStorage();

  useDebounce(
    () => {
      dispatch(
        fetchForums({ page, size, sortBy, sortDir, searchTerm, tagSearchTerm }),
      );
    },
    500,
    [page, size, sortBy, sortDir, searchTerm, tagSearchTerm],
  );

  useEffect(() => {
    dispatch(fetchFeaturedForums());
  }, [dispatch]);

  const handleSearch = (e: React.ChangeEvent<HTMLInputElement>) => {
    dispatch(setSearchTerm(e.target.value));
    dispatch(setPage(0));
  };

  const handleSortBy = useCallback(
    (val: string) => dispatch(setSortBy(val)),
    [dispatch],
  );
  const handleSortDir = useCallback(
    (val: string) => dispatch(setSortDir(val as "asc" | "desc")),
    [dispatch],
  );

  const handleTagSearch = (value: string) => {
    dispatch(setTagSearchTerm(value));
    dispatch(setPage(0));
  };

  const handleCreateForum = async () => {
    if (newForumContent.trim() === "") return;

    try {
      await dispatch(
        createForum({
          title: newForumTitle || "New Forum",
          content: newForumContent,
          tags: newForumTags,
        }),
      ).unwrap();

      resetEditor();
    } catch (err) {
      showToast("Failed to create forum", "destructive");
      console.error("Error creating forum:", err);
    }
  };

  const resetEditor = () => {
    setNewForumContent("");
    setNewForumTitle("");
    setNewForumTags(["community"]);
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
                placeholder="Search by 'title' or 'description'"
                className="border-0 bg-transparent px-1 focus-visible:ring-0 focus-visible:ring-offset-0"
                value={searchTerm}
                onChange={handleSearch}
              />
            </div>
            {searchTerm && (
              <p className="mt-2 text-sm text-gray-500">
                Found {totalElements ?? 0} results for "{searchTerm}"
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
          forums={featuredForums ?? []}
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
              forums={forums ?? []}
              loading={loading}
              error={error}
              sortBy={sortBy}
              sortDir={sortDir}
              onSortByChange={handleSortBy}
              onSortDirChange={handleSortDir}
              tagSearchTerm={tagSearchTerm}
              onTagSearchChange={handleTagSearch}
            />

            <PaginationControl
              currentPage={page}
              totalPages={totalPages}
              onPageChange={(p) => dispatch(setPage(p))}
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
