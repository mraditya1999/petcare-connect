import "react-quill/dist/quill.snow.css";
import { useCallback, useEffect, useState } from "react";
import { useAppDispatch, useAppSelector } from "@/app/hooks";
import { getUserFromStorage } from "@/utils/helpers";
import { SolvedTopics, Categories } from "@/components";
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
import { ForumListContainer } from "@/components/forum/ForumListContainer";
import CreateForumSection from "@/components/forum/CreateForumSection";
import ForumHeader from "@/components/forum/ForumHeader";
import ShowToast from "@/components/shared/ShowToast";

const ForumPage: React.FC = () => {
  const dispatch = useAppDispatch();
  const user = getUserFromStorage();

  const {
    forums,
    featuredForums,
    loading,
    error,
    page,
    size,
    totalPages,
    sortBy,
    sortDir,
    searchTerm,
    tagSearchTerm,
    totalElements,
  } = useAppSelector((state) => state.forumList);

  const [newForumTitle, setNewForumTitle] = useState("");
  const [newForumTags, setNewForumTags] = useState<string[]>(["community"]);
  const [newForumContent, setNewForumContent] = useState("");

  // Fetch forums debounced
  const fetchDebouncedForums = useCallback(() => {
    dispatch(
      fetchForums({ page, size, sortBy, sortDir, searchTerm, tagSearchTerm }),
    );
  }, [dispatch, page, size, sortBy, sortDir, searchTerm, tagSearchTerm]);

  useDebounce(fetchDebouncedForums, 500, [fetchDebouncedForums]);

  // Fetch featured forums on mount
  useEffect(() => {
    dispatch(fetchFeaturedForums());
  }, [dispatch]);

  // Handlers
  const handleSearch = (e: React.ChangeEvent<HTMLInputElement>) => {
    dispatch(setSearchTerm(e.target.value));
    dispatch(setPage(0));
  };

  const handleSortBy = useCallback(
    (val: string) => dispatch(setSortBy(val)),
    [dispatch],
  );
  const handleSortDir = useCallback(
    (val: "asc" | "desc") => dispatch(setSortDir(val)),
    [dispatch],
  );
  const handleTagSearch = (value: string) => {
    dispatch(setTagSearchTerm(value));
    dispatch(setPage(0));
  };

  const handleCreateForum = async () => {
    if (!newForumContent.trim()) return;

    try {
      await dispatch(
        createForum({
          title: newForumTitle || "New Forum",
          content: newForumContent,
          tags: newForumTags,
        }),
      ).unwrap();

      // Fetch forums again
      dispatch(
        fetchForums({
          page: 0,
          size,
          sortBy,
          sortDir,
          searchTerm,
          tagSearchTerm,
        }),
      );

      resetEditor();
      ShowToast({ description: "Forum created!", type: "success" });
    } catch (err) {
      ShowToast({ description: "Failed to create forum", type: "error" });
      console.error(err);
    }
  };

  const resetEditor = () => {
    setNewForumTitle("");
    setNewForumTags(["community"]);
    setNewForumContent("");
  };

  return (
    <div className="min-h-screen bg-white text-gray-900 dark:bg-gray-900 dark:text-gray-100">
      {/* Header */}
      <ForumHeader
        searchTerm={searchTerm}
        onSearchChange={handleSearch}
        totalElements={totalElements}
      />

      {/* Categories */}
      <section className="bg-gray-50 py-16 dark:bg-gray-800">
        <Categories />
      </section>

      {/* Featured Forums */}
      <section className="section-width py-16">
        <ForumListContainer
          title="Featured Forums"
          forums={featuredForums}
          loading={loading}
          error={error}
          page={0}
          totalPages={1}
          onPageChange={() => {}}
          onRetry={() => dispatch(fetchFeaturedForums())}
          emptyMessage="No featured forums available yet."
        />
      </section>

      {/* Main Forums + Solved Topics */}
      <section className="bg-gray-50 py-16 dark:bg-gray-800">
        <div className="section-width grid auto-rows-max items-start gap-8 lg:grid-cols-3">
          <article className="col-span-2">
            <ForumListContainer
              title="Recent activity"
              forums={forums}
              loading={loading}
              error={error}
              page={page}
              totalPages={totalPages}
              onPageChange={(p) => dispatch(setPage(p))}
              onRetry={fetchDebouncedForums}
              emptyMessage="No forums available yet."
              sortBy={sortBy}
              sortDir={sortDir}
              onSortByChange={handleSortBy}
              onSortDirChange={handleSortDir}
              tagSearchTerm={tagSearchTerm}
              onTagSearchChange={handleTagSearch}
            />
          </article>

          <article className="col-span-1 mt-32 h-auto rounded-xl bg-white p-4 shadow-sm dark:bg-gray-900 dark:shadow-none">
            <h2 className="mb-4 text-xl font-semibold text-gray-900 dark:text-gray-100">
              Solved topics
            </h2>
            <SolvedTopics />
          </article>
        </div>
      </section>

      {/* Create Forum */}
      {user && (
        <CreateForumSection
          title={newForumTitle}
          tags={newForumTags}
          content={newForumContent}
          setTitle={setNewForumTitle}
          setTags={setNewForumTags}
          setContent={setNewForumContent}
          onCreate={handleCreateForum}
          loading={loading}
        />
      )}
    </div>
  );
};

export default ForumPage;
