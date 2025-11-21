import { Input } from "@/components/ui/input";
import { FaMagnifyingGlass } from "react-icons/fa6";

interface SearchBarProps {
  searchTerm: string | undefined;
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  totalElements?: number;
  placeholder?: string;
  showResults?: boolean;
  darkMode?: boolean; // false = force light, true = follow theme
}

const SearchBar: React.FC<SearchBarProps> = ({
  searchTerm,
  onChange,
  totalElements,
  placeholder = "Search by 'title' or 'description'",
  showResults = true,
  darkMode = true, // default: follow theme
}) => {
  // If darkMode is false, force light classes (no dark: variants)
  const wrapperClasses = !darkMode
    ? "flex items-center gap-2 rounded-md border border-gray-200 bg-white px-3 py-2 shadow-sm transition-all hover:border-gray-300"
    : "flex items-center gap-2 rounded-md border border-gray-200 bg-white px-3 py-2 shadow-sm transition-all hover:border-gray-300 dark:border-gray-700 dark:bg-gray-800 dark:hover:border-gray-600";

  const iconClasses = !darkMode
    ? "text-gray-500"
    : "text-gray-500 dark:text-gray-300";

  const inputClasses = !darkMode
    ? "border-0 bg-transparent p-0 text-gray-800 placeholder-gray-400 focus-visible:ring-0 focus-visible:ring-offset-0"
    : "border-0 bg-transparent p-0 text-gray-800 placeholder-gray-400 focus-visible:ring-0 focus-visible:ring-offset-0 dark:text-gray-200 dark:placeholder-gray-500";

  const resultClasses = !darkMode
    ? "text-gray-600"
    : "text-gray-600 dark:text-gray-400";

  return (
    <div className="max-w-3xl">
      <div className={wrapperClasses}>
        <FaMagnifyingGlass className={iconClasses} />

        <Input
          placeholder={placeholder}
          className={inputClasses}
          value={searchTerm}
          onChange={onChange}
        />
      </div>

      {showResults && searchTerm && (
        <p className={`mt-2 text-sm ${resultClasses}`}>
          Found {totalElements ?? 0} results for "{searchTerm}"
        </p>
      )}
    </div>
  );
};

export default SearchBar;
