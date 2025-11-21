import "react-quill/dist/quill.snow.css";
import { useCallback, useEffect, useState } from "react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import forumHeaderImg from "@/assets/images/forumpage/forumheader.png";
import { useAppDispatch, useAppSelector } from "@/app/hooks";
import { getUserFromStorage, showToast } from "@/utils/helpers";
import { SolvedTopics, Categories, ForumEditor, SearchBar } from "@/components";
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

  // Debounced fetch
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
    <div className="min-h-screen bg-white text-gray-900 dark:bg-gray-900 dark:text-gray-100">
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
          <h1 className="mb-4 text-center text-4xl font-bold text-black dark:!text-black sm:text-6xl">
            Pet Care Connect
          </h1>

          <p className="tight mb-8 text-center text-xs text-black dark:!text-black md:text-sm">
            Share, Learn, and Connect with Fellow Pet Owners
          </p>

          <SearchBar
            searchTerm={searchTerm}
            onChange={handleSearch}
            totalElements={totalElements}
            darkMode={false}
          />
        </div>
      </section>

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
              onRetry={() =>
                dispatch(
                  fetchForums({
                    page,
                    size,
                    sortBy,
                    sortDir,
                    searchTerm,
                    tagSearchTerm,
                  }),
                )
              }
              emptyMessage="No forums available yet."
              sortBy={sortBy}
              sortDir={sortDir}
              onSortByChange={handleSortBy}
              onSortDirChange={handleSortDir}
              tagSearchTerm={tagSearchTerm}
              onTagSearchChange={handleTagSearch}
            />
          </article>

          {/* Solved Topics stays as-is */}
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
        <section className="bg-white py-16 dark:bg-gray-900">
          <div className="section-width mx-auto px-6">
            <h2 className="mb-4 text-xl font-semibold text-gray-900 dark:text-gray-100">
              Create a New Forum
            </h2>

            <Input
              placeholder="Forum Title"
              value={newForumTitle}
              onChange={(e) => setNewForumTitle(e.target.value)}
              className="mb-4 dark:border-gray-700 dark:bg-gray-800 dark:text-gray-100"
            />

            <Input
              placeholder="Tags (comma-separated)"
              value={newForumTags.join(",")}
              onChange={(e) => {
                const tags = e.target.value.split(",").map((tag) => tag.trim());
                setNewForumTags(tags);
              }}
              className="mb-4 dark:border-gray-700 dark:bg-gray-800 dark:text-gray-100"
            />

            <ForumEditor
              value={newForumContent}
              onChange={setNewForumContent}
            />

            <Button
              onClick={handleCreateForum}
              className="mt-4 bg-primary text-white hover:bg-primary/90 dark:bg-gray-200 dark:text-gray-900 dark:hover:bg-gray-300"
            >
              {loading ? "Creating..." : "Create Forum"}
            </Button>
          </div>
        </section>
      )}
    </div>
  );
};

export default ForumPage;
