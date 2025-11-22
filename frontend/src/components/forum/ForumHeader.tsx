/* eslint-disable @typescript-eslint/no-explicit-any */
import SearchBar from "../shared/SearchBar";
import forumHeaderImg from "@/assets/images/forumpage/forumheader.png";

const ForumHeader: React.FC<{
  searchTerm: string;
  onSearchChange: any;
  totalElements: number;
}> = ({ searchTerm, onSearchChange, totalElements }) => (
  <section
    className="flex h-96 max-h-[30rem] w-full items-center justify-center pt-16"
    style={{
      backgroundImage: `url('${forumHeaderImg}')`,
      backgroundPosition: "center",
      backgroundSize: "cover",
    }}
  >
    <div className="max-w-6xl px-4 text-center">
      <h1 className="mb-4 text-4xl font-bold text-black dark:text-black sm:text-6xl">
        Pet Care Connect
      </h1>
      <p className="tight mb-8 text-xs text-black dark:text-black md:text-sm">
        Share, Learn, and Connect with Fellow Pet Owners
      </p>
      <SearchBar
        searchTerm={searchTerm}
        onChange={onSearchChange}
        totalElements={totalElements}
        darkMode={false}
      />
    </div>
  </section>
);

export default ForumHeader;
