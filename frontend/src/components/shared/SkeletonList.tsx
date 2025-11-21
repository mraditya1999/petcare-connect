const ForumCardSkeleton = () => (
  <div className="animate-pulse rounded-xl border bg-white p-4 shadow-sm dark:bg-gray-800">
    <div className="mb-4 h-5 w-40 rounded bg-gray-300 dark:bg-gray-700"></div>
    <div className="mb-2 h-4 w-full rounded bg-gray-300 dark:bg-gray-700"></div>
    <div className="mb-4 h-4 w-5/6 rounded bg-gray-300 dark:bg-gray-700"></div>
    <div className="h-4 w-1/3 rounded bg-gray-300 dark:bg-gray-700"></div>
  </div>
);

const SkeletonList: React.FC<{ count?: number }> = ({ count = 5 }) => (
  <div className="flex flex-col gap-4">
    {Array.from({ length: count }).map((_, i) => (
      <ForumCardSkeleton key={i} />
    ))}
  </div>
);

export default SkeletonList;
