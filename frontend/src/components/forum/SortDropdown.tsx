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

interface SortDropdownProps {
  sortBy: string;
  sortDir: "asc" | "desc";
  onSortByChange: (field: string) => void;
  onSortDirChange: (dir: "asc" | "desc") => void;
}

const SortDropdown: FC<SortDropdownProps> = ({
  sortBy,
  sortDir,
  onSortByChange,
  onSortDirChange,
}) => {
  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button
          variant="outline"
          className="
            w-full sm:w-auto px-3 py-2 
            border-gray-300 dark:border-gray-700 
            bg-white dark:bg-gray-800 
            text-gray-800 dark:text-gray-200
            hover:bg-gray-100 dark:hover:bg-gray-700 
            transition-all
          "
        >
          <ArrowUpDown className="mr-2 h-4 w-4" />
          Sort
        </Button>
      </DropdownMenuTrigger>

      <DropdownMenuContent
        align="end"
        className="
          w-48 rounded-md border 
          border-gray-200 dark:border-gray-700 
          bg-white dark:bg-gray-800 
          shadow-lg dark:shadow-black/30 
          transition-all
        "
      >
        <DropdownMenuLabel className="text-gray-700 dark:text-gray-300">
          Sort by
        </DropdownMenuLabel>
        <DropdownMenuSeparator />

        {/* Newest */}
        <DropdownMenuItem
          onClick={() => onSortByChange("createdAt")}
          className={`
            cursor-pointer flex items-center gap-2 
            text-gray-700 dark:text-gray-300
            hover:bg-gray-100 dark:hover:bg-gray-700 
            transition-all
            ${sortBy === "createdAt" ? "bg-gray-100 dark:bg-gray-700" : ""}
          `}
        >
          {sortBy === "createdAt" && <Check className="h-4 w-4" />}
          Newest
        </DropdownMenuItem>

        {/* Uncomment these when you want additional sort options */}

        {/* <DropdownMenuItem
          onClick={() => onSortByChange("likesCount")}
          className={`
            cursor-pointer flex items-center gap-2 
            text-gray-700 dark:text-gray-300
            hover:bg-gray-100 dark:hover:bg-gray-700 
            transition-all
            ${sortBy === "likesCount" ? "bg-gray-100 dark:bg-gray-700" : ""}
          `}
        >
          {sortBy === "likesCount" && <Check className="h-4 w-4" />}
          Most Liked
        </DropdownMenuItem>

        <DropdownMenuItem
          onClick={() => onSortByChange("commentsCount")}
          className={`
            cursor-pointer flex items-center gap-2 
            text-gray-700 dark:text-gray-300
            hover:bg-gray-100 dark:hover:bg-gray-700 
            transition-all
            ${sortBy === "commentsCount" ? "bg-gray-100 dark:bg-gray-700" : ""}
          `}
        >
          {sortBy === "commentsCount" && <Check className="h-4 w-4" />}
          Most Commented
        </DropdownMenuItem> */}

        <DropdownMenuSeparator />
        <DropdownMenuLabel className="text-gray-700 dark:text-gray-300">
          Order
        </DropdownMenuLabel>

        {/* Ascending */}
        <DropdownMenuItem
          onClick={() => onSortDirChange("asc")}
          className={`
            cursor-pointer flex items-center gap-2
            text-gray-700 dark:text-gray-300
            hover:bg-gray-100 dark:hover:bg-gray-700 
            transition-all
            ${sortDir === "asc" ? "bg-gray-100 dark:bg-gray-700" : ""}
          `}
        >
          {sortDir === "asc" && <Check className="h-4 w-4" />}
          Ascending
        </DropdownMenuItem>

        {/* Descending */}
        <DropdownMenuItem
          onClick={() => onSortDirChange("desc")}
          className={`
            cursor-pointer flex items-center gap-2 
            text-gray-700 dark:text-gray-300
            hover:bg-gray-100 dark:hover:bg-gray-700 
            transition-all
            ${sortDir === "desc" ? "bg-gray-100 dark:bg-gray-700" : ""}
          `}
        >
          {sortDir === "desc" && <Check className="h-4 w-4" />}
          Descending
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  );
};

export default SortDropdown;
