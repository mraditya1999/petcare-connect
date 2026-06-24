import React from "react";
import { LoadingSpinner } from "@/components/ui/LoadingSpinner";
import { IForum } from "@/types/forum-types";
import { ForumCard, SearchBar, SortDropdown } from "@/components";

interface ForumSectionProps {
  title: string;
  forums: IForum[];
  loading: boolean;
  error: string | null;
  sortBy?: string;
  sortDir?: "asc" | "desc";
  onSortByChange?: (sortBy: string) => void;
  onSortDirChange?: (sortDir: "asc" | "desc") => void;
  tagSearchTerm?: string;
  onTagSearchChange?: (value: string) => void;
  emptyMessage?: string;
}

const ForumSection = ({
  title,
  forums = [],
  loading,
  error,
  emptyMessage = "No forums available yet.",
  sortBy,
  sortDir,
  onSortByChange,
  onSortDirChange,
  onTagSearchChange,
  tagSearchTerm,
}: ForumSectionProps) => {
  // show shimmer even during error
  const showShimmer = loading || !!error;

  const safeForums = Array.isArray(forums) ? forums : [];

  return (
    <div>
      <h2 className="mb-6 text-2xl font-semibold text-gray-900 dark:text-gray-100">
        {title}
      </h2>

      {/* SORT + TAG SEARCH */}
      <div className="mb-4 flex items-center justify-between gap-4">
        {sortBy && sortDir && onSortByChange && onSortDirChange && (
          <SortDropdown
            sortBy={sortBy}
            sortDir={sortDir}
            onSortByChange={onSortByChange}
            onSortDirChange={onSortDirChange}
            sortByOptions={[
              { label: "Newest", value: "createdAt" },
              { label: "Top Rated", value: "rating" },
            ]}
            sortDirOptions={[
              { label: "Ascending", value: "asc" },
              { label: "Descending", value: "desc" },
            ]}
          />
        )}

        {onTagSearchChange && (
          <SearchBar
            searchTerm={tagSearchTerm}
            onChange={(e) => onTagSearchChange(e.target.value)}
            placeholder="Search by tags (comma-separated)"
            showResults={false}
            darkMode={true}
          />
        )}
      </div>

      {/* ERROR TEXT */}
      {error && (
        <p className="mb-3 rounded-md bg-red-100 p-2 text-sm text-red-600 dark:bg-red-900/40 dark:text-red-400">
          {error}
        </p>
      )}

      {/* SHIMMER for loading + error */}
      {showShimmer ? (
        <div className="flex justify-center py-6">
          <LoadingSpinner />
        </div>
      ) : safeForums.length === 0 ? (
        <p className="text-sm text-gray-500 dark:text-gray-400">
          {emptyMessage}
        </p>
      ) : (
        <ForumCard forums={safeForums} />
      )}
    </div>
  );
};

export default React.memo(ForumSection);
