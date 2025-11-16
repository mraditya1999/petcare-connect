import { LoadingSpinner } from "@/components/ui/LoadingSpinner";
import ForumCard from "@/components/forum/ForumCard";
import { IForum } from "@/types/forum-types";
import SortDropdown from "./SortDropdown";
import React from "react";
import SearchBar from "../shared/SearchBar";

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
  return (
    <div>
      <div>
        <h2 className="mb-6 text-2xl font-semibold">{title}</h2>

        <div className="mb-4 flex items-center justify-between">
          {sortBy && sortDir && onSortByChange && onSortDirChange && (
            <SortDropdown
              sortBy={sortBy}
              sortDir={sortDir}
              onSortByChange={onSortByChange}
              onSortDirChange={onSortDirChange}
            />
          )}

          {onTagSearchChange && (
            <SearchBar
              searchTerm={tagSearchTerm}
              onChange={(e) => onTagSearchChange(e.target.value)}
              placeholder="Search by tags (comma-separated)"
              showResults={false} // don’t show “Found X results” line
            />
          )}
        </div>
      </div>

      {loading ? (
        <div className="flex justify-center">
          <LoadingSpinner />
        </div>
      ) : error ? (
        <p className="rounded-md bg-red-100 p-2 text-sm text-red-500">
          {error}
        </p>
      ) : forums.length === 0 ? (
        <p className="text-sm text-gray-500">{emptyMessage}</p>
      ) : (
        <ForumCard forums={forums} />
      )}
    </div>
  );
};

export default React.memo(ForumSection);
