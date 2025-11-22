import { FC } from "react";
import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { ArrowUpDown, Check } from "lucide-react";

export interface SortOption {
  label: string;
  value: string;
}

interface SortDropdownProps {
  sortBy: string;
  sortDir: "asc" | "desc";
  onSortByChange: (field: string) => void;
  onSortDirChange: (dir: "asc" | "desc") => void;
  sortByOptions?: SortOption[];
  sortDirOptions?: SortOption[];
}

const SortDropdown: FC<SortDropdownProps> = ({
  sortBy,
  sortDir,
  onSortByChange,
  onSortDirChange,
  sortByOptions = [
    { label: "Newest", value: "createdAt" },
    { label: "Most Liked", value: "likesCount" },
    { label: "Most Commented", value: "commentsCount" },
  ],
  sortDirOptions = [
    { label: "Ascending", value: "asc" },
    { label: "Descending", value: "desc" },
  ],
}) => {
  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button
          variant="outline"
          className="w-full border-gray-300 bg-white px-3 py-2 text-gray-800 transition-all hover:bg-gray-100 dark:border-gray-700 dark:bg-gray-800 dark:text-gray-200 dark:hover:bg-gray-700 sm:w-auto"
        >
          <ArrowUpDown className="mr-2 h-4 w-4" />
          Sort
        </Button>
      </DropdownMenuTrigger>

      <DropdownMenuContent
        align="end"
        className="w-48 rounded-md border border-gray-200 bg-white shadow-lg transition-all dark:border-gray-700 dark:bg-gray-800 dark:shadow-black/30"
      >
        {/* Sort By */}
        <DropdownMenuLabel className="text-gray-700 dark:text-gray-300">
          Sort by
        </DropdownMenuLabel>
        <DropdownMenuSeparator />
        {sortByOptions.map((option) => (
          <DropdownMenuItem
            key={option.value}
            onClick={() => onSortByChange(option.value)}
            className={`flex cursor-pointer items-center gap-2 text-gray-700 transition-all hover:bg-gray-100 dark:text-gray-300 dark:hover:bg-gray-700 ${sortBy === option.value ? "bg-gray-100 dark:bg-gray-700" : ""}`}
          >
            {sortBy === option.value && <Check className="h-4 w-4" />}
            {option.label}
          </DropdownMenuItem>
        ))}

        {/* Sort Direction */}
        <DropdownMenuSeparator />
        <DropdownMenuLabel className="text-gray-700 dark:text-gray-300">
          Order
        </DropdownMenuLabel>
        {sortDirOptions.map((option) => (
          <DropdownMenuItem
            key={option.value}
            onClick={() => onSortDirChange(option.value as "asc" | "desc")}
            className={`flex cursor-pointer items-center gap-2 text-gray-700 transition-all hover:bg-gray-100 dark:text-gray-300 dark:hover:bg-gray-700 ${sortDir === option.value ? "bg-gray-100 dark:bg-gray-700" : ""}`}
          >
            {sortDir === option.value && <Check className="h-4 w-4" />}
            {option.label}
          </DropdownMenuItem>
        ))}
      </DropdownMenuContent>
    </DropdownMenu>
  );
};

export default SortDropdown;
