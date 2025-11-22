import { FC } from "react";
import {
  DropdownMenu,
  DropdownMenuTrigger,
  DropdownMenuContent,
  DropdownMenuItem,
} from "@/components/ui/dropdown-menu";
import { FaEllipsisVertical } from "react-icons/fa6";

export type EllipsisDropdownItem = {
  label: string;
  onClick: () => void;
  variant?: "destructive";
};

export interface EllipsisDropdownProps {
  items: EllipsisDropdownItem[];
  buttonClassName?: string;
}

const EllipsisDropdown: FC<EllipsisDropdownProps> = ({
  items,
  buttonClassName,
}) => {
  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <button
          type="button"
          className={
            buttonClassName || "text-muted-foreground hover:text-foreground"
          }
        >
          <FaEllipsisVertical size={18} />
        </button>
      </DropdownMenuTrigger>

      <DropdownMenuContent align="end" className="w-32">
        {items.map((item, index) => (
          <DropdownMenuItem
            key={index}
            onClick={item.onClick}
            className={item.variant === "destructive" ? "text-destructive" : ""}
          >
            {item.label}
          </DropdownMenuItem>
        ))}
      </DropdownMenuContent>
    </DropdownMenu>
  );
};

export default EllipsisDropdown;
