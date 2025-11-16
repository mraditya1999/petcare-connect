// import { FaMagnifyingGlass } from "react-icons/fa6";
// import { Input } from "@/components/ui/input";

// interface SearchBarProps {
//   searchTerm: string;
//   onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
//   totalElements?: number;
// }

// const SearchBar: React.FC<SearchBarProps> = ({
//   searchTerm,
//   onChange,
//   totalElements,
// }) => {
//   return (
//     <div className="max-w-3xl">
//       <div className="flex items-center gap-1 rounded-md bg-white p-2">
//         <FaMagnifyingGlass className="h-5 w-5 text-gray-400" />
//         <Input
//           placeholder="Search by 'title' or 'description'"
//           className="border-0 bg-transparent px-1 focus-visible:ring-0 focus-visible:ring-offset-0"
//           value={searchTerm}
//           onChange={onChange}
//         />
//       </div>
//       {searchTerm && (
//         <p className="mt-2 text-sm text-gray-500">
//           Found {totalElements ?? 0} results for "{searchTerm}"
//         </p>
//       )}
//     </div>
//   );
// };

// export default SearchBar;
import { Input } from "@/components/ui/input";
import { FaMagnifyingGlass } from "react-icons/fa6";

interface SearchBarProps {
  searchTerm: string | undefined;
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  totalElements?: number;
  placeholder?: string;
  showResults?: boolean; // default true
}

const SearchBar: React.FC<SearchBarProps> = ({
  searchTerm,
  onChange,
  totalElements,
  placeholder = "Search by 'title' or 'description'",
  showResults = true,
}) => {
  return (
    <div className="max-w-3xl">
      <div className="flex items-center gap-1 rounded-md bg-white p-2">
        <FaMagnifyingGlass />
        <Input
          placeholder={placeholder}
          className="border-0 bg-transparent px-1 focus-visible:ring-0 focus-visible:ring-offset-0"
          value={searchTerm}
          onChange={onChange}
        />
      </div>
      {showResults && searchTerm && (
        <p className="mt-2 text-sm text-gray-500">
          Found {totalElements ?? 0} results for "{searchTerm}"
        </p>
      )}
    </div>
  );
};

export default SearchBar;
