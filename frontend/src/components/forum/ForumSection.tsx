import { LoadingSpinner } from "@/components/ui/LoadingSpinner";
import ForumCard from "@/components/forum/ForumCard";
import { IForum } from "@/types/forum-types";
import SortDropdown from "./SortDropdown";

interface ForumSectionProps {
  title: string;
  forums: IForum[];
  loading: boolean;
  error: string | null;
  emptyMessage?: string;
  sortBy?: string;
  sortDir?: "asc" | "desc";
  onSortByChange?: (field: string) => void;
  onSortDirChange?: (dir: "asc" | "desc") => void;
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
}: ForumSectionProps) => {
  return (
    <div>
      <div className="flex items-center justify-between">
        <h2 className="mb-6 text-2xl font-semibold">{title}</h2>
        {sortBy && sortDir && onSortByChange && onSortDirChange && (
          <div className="mb-6">
            <SortDropdown
              sortBy={sortBy}
              sortDir={sortDir}
              onSortByChange={onSortByChange}
              onSortDirChange={onSortDirChange}
            />
          </div>
        )}
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
