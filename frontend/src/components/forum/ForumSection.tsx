import { LoadingSpinner } from "@/components/ui/LoadingSpinner";
import ForumCard from "@/components/forum/ForumCard";
import { IForum } from "@/types/forum-types";
import SortDropdown from "./SortDropdown";
import { Search } from "lucide-react";
import { Input } from "../ui/input";

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
  onTagSearchChange?: (value: string) => void; // <-- new prop
  emptyMessage?: string;
}

const ForumSection = ({
  title,
  forums,
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
      <div className="">
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
          {/* Search Bar */}
          {onTagSearchChange && (
            // <div className="flex items-center gap-1 rounded-md bg-red-400 p-2">
            <div className="flex items-center gap-1 rounded-md border bg-white p-1">
              <Search className="h-5 w-5 text-gray-400" />
              <Input
                placeholder="Search by tags (comma-separated)"
                className="border-0 bg-transparent px-1 focus-visible:ring-0 focus-visible:ring-offset-0"
                value={tagSearchTerm}
                onChange={(e) =>
                  onTagSearchChange && onTagSearchChange(e.target.value)
                }
              />
            </div>
            // </div>
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

export default ForumSection;
