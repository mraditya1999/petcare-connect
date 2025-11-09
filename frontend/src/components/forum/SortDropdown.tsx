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
        <Button variant="outline" className="w-full sm:w-auto">
          <ArrowUpDown className="mr-2 h-4 w-4" />
          Sort
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end" className="w-44">
        <DropdownMenuLabel>Sort by</DropdownMenuLabel>
        <DropdownMenuSeparator />

        <DropdownMenuItem
          onClick={() => onSortByChange("createdAt")}
          className="cursor-pointer"
        >
          {sortBy === "createdAt" && <Check className="mr-2 h-4 w-4" />}
          Newest
        </DropdownMenuItem>

        <DropdownMenuItem
          onClick={() => onSortByChange("likesCount")}
          className="cursor-pointer"
        >
          {sortBy === "likesCount" && <Check className="mr-2 h-4 w-4" />}
          Most Liked
        </DropdownMenuItem>

        <DropdownMenuItem
          onClick={() => onSortByChange("commentsCount")}
          className="cursor-pointer"
        >
          {sortBy === "commentsCount" && <Check className="mr-2 h-4 w-4" />}
          Most Commented
        </DropdownMenuItem>

        <DropdownMenuSeparator />
        <DropdownMenuLabel>Order</DropdownMenuLabel>

        <DropdownMenuItem
          onClick={() => onSortDirChange("asc")}
          className="cursor-pointer"
        >
          {sortDir === "asc" && <Check className="mr-2 h-4 w-4" />}
          Ascending
        </DropdownMenuItem>

        <DropdownMenuItem
          onClick={() => onSortDirChange("desc")}
          className="cursor-pointer"
        >
          {sortDir === "desc" && <Check className="mr-2 h-4 w-4" />}
          Descending
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  );
};

export default SortDropdown;
