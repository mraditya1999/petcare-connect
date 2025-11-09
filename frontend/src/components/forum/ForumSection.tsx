import { LoadingSpinner } from "@/components/ui/LoadingSpinner";
import ForumCard from "@/components/forum/ForumCard";
import { IForum } from "@/types/forum-types";

interface ForumSectionProps {
  title: string;
  forums: IForum[];
  loading: boolean;
  error: string | null;
  emptyMessage?: string;
}

const ForumSection = ({
  title,
  forums,
  loading,
  error,
  emptyMessage = "No forums available yet.",
}: ForumSectionProps) => {
  return (
    <div>
      <h2 className="mb-6 text-2xl font-semibold">{title}</h2>
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
