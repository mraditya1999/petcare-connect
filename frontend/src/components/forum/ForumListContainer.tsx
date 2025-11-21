/* eslint-disable @typescript-eslint/no-explicit-any */
import { ForumSection, PaginationControl } from "@/components";
import SkeletonList from "../shared/SkeletonList";
import ErrorFallback from "../shared/ErrorFallback";

interface ForumListContainerProps {
  title: string;
  forums: any[];
  loading: boolean;
  error: string | null;
  page: number;
  totalPages: number;
  onPageChange: (page: number) => void;
  onRetry: () => void;
  emptyMessage?: string;
  sortBy?: string;
  sortDir?: "asc" | "desc";
  onSortByChange?: (val: string) => void;
  onSortDirChange?: (val: "asc" | "desc") => void;
  tagSearchTerm?: string;
  onTagSearchChange?: (val: string) => void;
}

export const ForumListContainer: React.FC<ForumListContainerProps> = ({
  title,
  forums,
  loading,
  error,
  page,
  totalPages,
  onPageChange,
  onRetry,
  emptyMessage,
  sortBy,
  sortDir,
  onSortByChange,
  onSortDirChange,
  tagSearchTerm,
  onTagSearchChange,
}) => {
  return (
    <div>
      {loading && !forums?.length ? (
        <SkeletonList count={6} />
      ) : error ? (
        <div>
          <SkeletonList count={6} />
          <ErrorFallback
            message={`We couldn’t load ${title.toLowerCase()} right now.`}
            onRetry={onRetry}
          />
        </div>
      ) : (
        <ForumSection
          title={title}
          forums={forums ?? []}
          loading={loading}
          error={error}
          emptyMessage={emptyMessage}
          sortBy={sortBy}
          sortDir={sortDir}
          onSortByChange={onSortByChange}
          onSortDirChange={onSortDirChange}
          tagSearchTerm={tagSearchTerm}
          onTagSearchChange={onTagSearchChange}
        />
      )}

      <PaginationControl
        currentPage={page}
        totalPages={totalPages}
        onPageChange={onPageChange}
      />
    </div>
  );
};
