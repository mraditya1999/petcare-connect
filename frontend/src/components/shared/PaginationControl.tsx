import React from "react";
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationPrevious,
  PaginationNext,
  PaginationLink,
} from "@/components/ui/pagination";

interface PaginationControlProps {
  currentPage: number;
  totalPages: number;
  onPageChange: (page: number) => void;
}

const PaginationControl: React.FC<PaginationControlProps> = ({
  currentPage,
  totalPages,
  onPageChange,
}) => {
  if (totalPages <= 1) return null;

  return (
    <div className="mt-6 flex justify-center">
      <Pagination>
        <PaginationContent>
          {/* Previous */}
          <PaginationItem>
            <PaginationPrevious
              href="#"
              onClick={(e) => {
                e.preventDefault();
                if (currentPage > 0) onPageChange(currentPage - 1);
              }}
              className={`flex items-center gap-1 ${
                currentPage === 0 ? "pointer-events-none opacity-50" : ""
              }`}
            >
              ← <span className="hidden sm:inline">Previous</span>
            </PaginationPrevious>
          </PaginationItem>

          {/* Page Numbers */}
          {[...Array(totalPages)].map((_, index) => (
            <PaginationItem key={index}>
              <PaginationLink
                href="#"
                isActive={index === currentPage}
                onClick={(e) => {
                  e.preventDefault();
                  onPageChange(index);
                }}
              >
                {index + 1}
              </PaginationLink>
            </PaginationItem>
          ))}

          {/* Next */}
          <PaginationItem>
            <PaginationNext
              href="#"
              onClick={(e) => {
                e.preventDefault();
                if (currentPage < totalPages - 1) onPageChange(currentPage + 1);
              }}
              className={`flex items-center gap-1 ${
                currentPage === totalPages - 1
                  ? "pointer-events-none opacity-50"
                  : ""
              }`}
            >
              <span className="hidden sm:inline">Next</span> →
            </PaginationNext>
          </PaginationItem>
        </PaginationContent>
      </Pagination>
    </div>
  );
};

export default PaginationControl;
