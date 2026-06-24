import { FC, useState } from "react";
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
  const [open, setOpen] = useState(false);

  return (
    <DropdownMenu open={open} onOpenChange={setOpen}>
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
            onSelect={(e) => {
              if (item.variant === "destructive") {
                e.preventDefault();
              }
              item.onClick();
            }}
            className={
              item.variant === "destructive"
                ? "text-destructive focus:bg-destructive/10"
                : ""
            }
          >
            {item.label}
          </DropdownMenuItem>
        ))}
      </DropdownMenuContent>
    </DropdownMenu>
  );
};

export default EllipsisDropdown;
