import { Skeleton } from "@/components/ui/skeleton";

const ProfileShimmer = () => {
  return (
    <div className="mt-6 grid grid-cols-1 gap-4 sm:grid-cols-2">
      <div className="flex items-center justify-center">
        <Skeleton className="mt-2 h-32 w-32 rounded-full" />
      </div>

      <div>
        <Skeleton className="h-6 w-1/2" />
        <Skeleton className="mt-2 h-8 w-full" />
      </div>
      <div>
        <Skeleton className="h-6 w-1/2" />
        <Skeleton className="mt-2 h-8 w-full" />
      </div>
      <div>
        <Skeleton className="h-6 w-1/2" />
        <Skeleton className="mt-2 h-8 w-full" />
      </div>
      <div>
        <Skeleton className="h-6 w-1/2" />
        <Skeleton className="mt-2 h-8 w-full" />
      </div>
      <div>
        <Skeleton className="h-6 w-1/2" />
        <Skeleton className="mt-2 h-8 w-full" />
      </div>
      <div>
        <Skeleton className="h-6 w-1/2" />
        <Skeleton className="mt-2 h-8 w-full" />
      </div>
      <div>
        <Skeleton className="h-6 w-1/2" />
        <Skeleton className="mt-2 h-8 w-full" />
      </div>
      <div>
        <Skeleton className="h-6 w-1/2" />
        <Skeleton className="mt-2 h-8 w-full" />
      </div>
      <div>
        <Skeleton className="h-6 w-1/2" />
        <Skeleton className="mt-2 h-8 w-full" />
      </div>
      <div className="col-span-1 flex justify-between space-x-2 sm:col-span-2">
        <Skeleton className="h-10 w-32" />
        <Skeleton className="h-10 w-32" />
      </div>
    </div>
  );
};

export default ProfileShimmer;
