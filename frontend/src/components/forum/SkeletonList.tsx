import { cn } from "@/lib/utils";

export function Skeleton({
  className,
  ...props
}: React.HTMLAttributes<HTMLDivElement>) {
  return (
    <div
      className={cn("animate-pulse rounded-md bg-muted", className)}
      {...props}
    />
  );
}

const ForumCardSkeleton = () => (
  <div className="rounded-xl border bg-card p-4 shadow-sm">
    <Skeleton className="mb-4 h-5 w-40" />
    <Skeleton className="mb-2 h-4 w-full" />
    <Skeleton className="mb-4 h-4 w-5/6" />
    <Skeleton className="h-4 w-1/3" />
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
