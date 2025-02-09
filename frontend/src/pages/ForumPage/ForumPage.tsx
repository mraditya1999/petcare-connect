// import { useEffect, useState } from "react";
// import { customFetch } from "@/utils/customFetch";
// import { Input } from "@/components/ui/input";
// import { Card } from "@/components/ui/card";
// import { Search, MessageCircle, Eye, Check } from "lucide-react";
// import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
// import { Button } from "@/components/ui/button";
// import ReactQuill from "react-quill";
// import "react-quill/dist/quill.snow.css";
// import { IForum } from "@/types/forum-types";
// import { Link } from "react-router-dom";

// import forumHeaderImg from "@/assets/images/forumpage/forumheader.png";
// import { FaRegHeart, FaRegMessage } from "react-icons/fa6";
// import img1 from "@/assets/images/forumpage/img1.png";
// import img2 from "@/assets/images/forumpage/img2.png";
// import img3 from "@/assets/images/forumpage/img3.png";
// import img4 from "@/assets/images/forumpage/img4.png";
// import img5 from "@/assets/images/forumpage/img5.png";
// import img6 from "@/assets/images/forumpage/img6.png";
// import { formatRelativeTime } from "@/utils/helpers";
// import { ROUTES } from "@/utils/constants";

// const ForumPage = () => {
//   const [forums, setForums] = useState<IForum[]>([]);
//   const [loading, setLoading] = useState<boolean>(true);
//   const [error, setError] = useState<string | null>(null);
//   const [newForumContent, setNewForumContent] = useState("");

//   const categories = [
//     {
//       title: "General Discussion",
//       src: img1,
//     },
//     {
//       title: "Health & Nutrition",
//       src: img2,
//     },
//     {
//       title: "Showcase Your Care",
//       src: img3,
//     },
//     {
//       title: "Training & Behavior",
//       src: img4,
//     },
//     {
//       title: "Breed-Specific Discussions",
//       src: img5,
//     },
//     {
//       title: "Adoption & Rescue",
//       src: img6,
//     },
//   ];

//   const featuredTopics = [
//     "Mood of the Month",
//     "Product Reviews and Recommendations",
//     "How do you adapt your care routine for senior pets to keep them happy and healthy?",
//   ];

//   const solvedTopics = [
//     "Free consultation does my puppy need",
//     "How can I socialize my rescue pet with other animals?",
//     "What are the best local parks for dog walking?",
//     "What are some fun indoor activities for dogs?",
//     "What are some engaging toys for cats that keep them engaged?",
//     "Natural remedies for itching",
//   ];

//   const fetchForums = async () => {
//     try {
//       const response = await customFetch("/forums");
//       if (!Array.isArray(response.data)) throw new Error("Invalid data format");
//       setForums(response.data);
//     } catch (error) {
//       setError("Error fetching forums. Please try again.");
//     } finally {
//       setLoading(false);
//     }
//   };

//   useEffect(() => {
//     fetchForums();
//   }, []);

//   const handleCreateForum = async () => {
//     if (newForumContent.trim() === "") return;
//     try {
//       await customFetch.post("/forums", {
//         title: "New Forum", // You may want to make this dynamic
//         content: newForumContent,
//         tags: ["community"], // Default or selected tags
//       });
//       setNewForumContent("");
//       fetchForums(); // Refetch forums
//     } catch (error) {
//       console.error("Error creating forum:", error);
//     }
//   };

//   return (
//     <div className="min-h-screen">
//       {/* Header */}
//       <section
//         className="flex h-96 max-h-[30rem] w-full items-center justify-center pt-16"
//         style={{
//           backgroundImage: `url('${forumHeaderImg}')`,
//           backgroundPosition: "center",
//           backgroundSize: "cover",
//         }}
//       >
//         <div className="max-w-6xl px-4">
//           <h1 className="mb-4 text-center text-4xl font-bold sm:text-6xl">
//             Pet Care Connect
//           </h1>
//           <p className="tight mb-8 text-center text-xs text-gray-600 md:text-sm">
//             Share, Learn, and Connect with Fellow Pet Owners
//           </p>

//           {/* Search Bar */}
//           <div className="max-w-3xl">
//             <div className="flex items-center gap-1 rounded-md bg-white p-2">
//               <Search className="h-5 w-5 text-gray-400" />
//               <Input
//                 placeholder="Search everything pet care related..."
//                 className="border-0 bg-transparent px-1 focus-visible:ring-0 focus-visible:ring-offset-0"
//               />
//             </div>
//           </div>
//         </div>
//       </section>

//       {/* Services */}
//       <section className="py-16">
//         {/* Categories */}
//         <div className="section-width mx-auto grid max-w-6xl grid-cols-2 gap-6 px-6 lg:grid-cols-3">
//           {categories.map((category, index) => (
//             <Card
//               key={index}
//               className="cursor-pointer border border-0 p-6 shadow transition duration-300 hover:shadow-lg"
//             >
//               <div className="flex flex-col items-center gap-4">
//                 <img src={category.src} alt={category.title} />
//                 <h3 className="text-center font-medium">{category.title}</h3>
//               </div>
//             </Card>
//           ))}
//         </div>
//       </section>

//       {/* Featured Topics */}
//       <section className="py-16">
//         <div className="section-width">
//           <h2 className="mb-6 text-2xl font-semibold">Featured topics</h2>
//           <div className="space-y-3">
//             {featuredTopics.map((topic, index) => (
//               <Card
//                 key={index}
//                 className="cursor-pointer border border-0 bg-white p-4"
//               >
//                 <div className="flex items-center justify-between">
//                   <div className="flex items-center gap-2">
//                     <Avatar className="h-8 w-8 text-gray-600">
//                       <AvatarImage src="https://github.com/shadcn.png" />
//                       <AvatarFallback>CN</AvatarFallback>
//                     </Avatar>
//                     <p className="text-sm">{topic}</p>
//                   </div>
//                   <div className="flex items-center gap-4 text-gray-500">
//                     <span className="flex items-center gap-1">
//                       <MessageCircle className="h-4 w-4" /> 12
//                     </span>
//                     <span className="flex items-center gap-1">
//                       <Eye className="h-4 w-4" /> 45
//                     </span>
//                   </div>
//                 </div>
//               </Card>
//             ))}
//           </div>
//         </div>
//       </section>

//       {/* Two Column Layout */}
//       <section className="py-16">
//         <div className="section-width grid gap-8 lg:col-span-2 lg:grid-cols-3">
//           {/* Display Forums */}
//           <article className="col-span-2">
//             <h2 className="mb-4 text-xl font-semibold">Recent activity</h2>
//             <div className="space-y-3">
//               {forums.map((forum) => (
//                 <Link
//                   key={forum.forumId}
//                   to={`${ROUTES.FORUM}/${forum.forumId}`}
//                   className="block"
//                 >
//                   <Card key={forum.forumId} className="border-0 bg-white p-4">
//                     <div className="flex items-center gap-4">
//                       <div className="flex h-8 w-8 items-center justify-center rounded-full bg-blue-100 text-blue-600">
//                         <Avatar className="h-8 w-8">
//                           <AvatarImage
//                             src={forum.userProfile || ""}
//                             alt={forum.firstName}
//                           />
//                           <AvatarFallback>
//                             {forum.firstName?.charAt(0) || "?"}
//                           </AvatarFallback>
//                         </Avatar>
//                       </div>
//                       <div className="flex-grow">
//                         <h3 className="mb-2 text-sm font-semibold">
//                           {forum.firstName} {forum.lastName}
//                         </h3>
//                         <p className="text-sm text-gray-900">{forum.title}</p>
//                         <div className="mt-2 flex items-center gap-4 text-sm text-xs text-gray-500">
//                           <Button
//                             variant="ghost"
//                             className="flex w-auto items-center gap-1 p-0 hover:bg-transparent"
//                           >
//                             <FaRegHeart className="h-4 w-4" />
//                             {forum.likes?.length || 0}
//                           </Button>
//                           <Button
//                             variant="ghost"
//                             className="flex w-auto items-center gap-1 p-0 hover:bg-transparent"
//                           >
//                             <FaRegMessage className="h-4 w-4" />
//                             {forum.comments?.length || 0}
//                           </Button>
//                           <span className="flex items-center gap-1">
//                             {formatRelativeTime(forum.createdAt)}
//                           </span>
//                         </div>
//                       </div>
//                     </div>
//                   </Card>
//                 </Link>
//               ))}
//             </div>
//           </article>

//           {/* Solved Topics */}
//           <article className="col-span-1">
//             <h2 className="mb-4 text-xl font-semibold">Solved topics</h2>
//             <div className="space-y-3">
//               {solvedTopics.map((topic, index) => (
//                 <Card
//                   key={index}
//                   className="cursor-pointer border-0 bg-white p-4"
//                 >
//                   <div className="flex items-start gap-2">
//                     <Check className="mt-1 h-4 w-4 flex-shrink-0 text-green-500" />
//                     <p className="text-sm">{topic}</p>
//                   </div>
//                 </Card>
//               ))}
//             </div>
//           </article>
//         </div>
//       </section>

//       {/* Create Forum */}
//       {/* <section className="py-16">
//         <div className="section-width mx-auto px-6">
//           <h2 className="mb-4 text-xl font-semibold">Create a New Forum</h2>
//           <div className="mb-6">
//             <ReactQuill
//               value={newForumContent}
//               onChange={setNewForumContent}
//               modules={{
//                 toolbar: [
//                   ["bold", "italic"],
//                   [{ list: "ordered" }, { list: "bullet" }],
//                   ["link"],
//                 ],
//               }}
//               formats={["bold", "italic", "list", "bullet", "link"]}
//               placeholder="Write your forum content here..."
//             />
//           </div>
//           <Button onClick={handleCreateForum}>Create Forum</Button>
//         </div>
//       </section> */}

//       <section className="py-16">
//         <div className="section-width mx-auto px-6">
//           <h2 className="mb-4 text-xl font-semibold">Create a New Forum</h2>
//           <div className="mb-4 min-h-[13rem]">
//             {" "}
//             {/* Use min-h instead of fixed h */}
//             <ReactQuill
//               className="h-full"
//               value={newForumContent}
//               onChange={setNewForumContent}
//               modules={{
//                 toolbar: [
//                   ["bold", "italic"],
//                   [{ list: "ordered" }, { list: "bullet" }],
//                   ["link"],
//                 ],
//               }}
//               formats={["bold", "italic", "list", "bullet", "link"]}
//               placeholder="Write your forum content here..."
//             />
//           </div>
//           <Button onClick={handleCreateForum} className="mt-4">
//             Create Forum
//           </Button>
//         </div>
//       </section>
//     </div>
//   );
// };

// export default ForumPage;

import { useEffect, useState } from "react";
import { customFetch } from "@/utils/customFetch";
import { Input } from "@/components/ui/input";
import { Card } from "@/components/ui/card";
import { Search, MessageCircle, Eye, Check } from "lucide-react";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import ReactQuill from "react-quill";
import "react-quill/dist/quill.snow.css";
import { IForum } from "@/types/forum-types";
import { Link } from "react-router-dom";
import "react-quill/dist/quill.snow.css";
import forumHeaderImg from "@/assets/images/forumpage/forumheader.png";
import { FaRegHeart, FaRegMessage } from "react-icons/fa6";
import img1 from "@/assets/images/forumpage/img1.png";
import img2 from "@/assets/images/forumpage/img2.png";
import img3 from "@/assets/images/forumpage/img3.png";
import img4 from "@/assets/images/forumpage/img4.png";
import img5 from "@/assets/images/forumpage/img5.png";
import img6 from "@/assets/images/forumpage/img6.png";
import { formatRelativeTime } from "@/utils/helpers";
import { ROUTES } from "@/utils/constants";

const ForumPage = () => {
  const [newForumTitle, setNewForumTitle] = useState("New Forum"); // Default title
  const [newForumTags, setNewForumTags] = useState(["community"]); // Default tags
  const [forums, setForums] = useState<IForum[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [newForumContent, setNewForumContent] = useState("");
  const [featuredForums, setFeaturedForums] = useState<IForum[]>([]); // State for featured forums

  const categories = [
    {
      title: "General Discussion",
      src: img1,
    },
    {
      title: "Health & Nutrition",
      src: img2,
    },
    {
      title: "Showcase Your Care",
      src: img3,
    },
    {
      title: "Training & Behavior",
      src: img4,
    },
    {
      title: "Breed-Specific Discussions",
      src: img5,
    },
    {
      title: "Adoption & Rescue",
      src: img6,
    },
  ];

  const featuredTopics = [
    "Mood of the Month",
    "Product Reviews and Recommendations",
    "How do you adapt your care routine for senior pets to keep them happy and healthy?",
  ];

  const solvedTopics = [
    "Free consultation does my puppy need",
    "How can I socialize my rescue pet with other animals?",
    "What are the best local parks for dog walking?",
    "What are some fun indoor activities for dogs?",
    "What are some engaging toys for cats that keep them engaged?",
    "Natural remedies for itching",
  ];

  const fetchForums = async () => {
    try {
      const response = await customFetch("/forums");
      if (!Array.isArray(response.data)) throw new Error("Invalid data format");
      setForums(response.data);
      setFeaturedForums(response.data.slice(0, 3));
    } catch (error) {
      setError("Error fetching forums. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchForums();
  }, []);

  const handleCreateForum = async () => {
    if (newForumContent.trim() === "") return;

    try {
      await customFetch.post("/forums", {
        title: newForumTitle,
        content: newForumContent,
        tags: newForumTags,
      });
      setNewForumContent("");
      setNewForumTitle("New Forum"); // Reset title
      setNewForumTags(["community"]); // Reset tags
      fetchForums();
    } catch (error) {
      console.error("Error creating forum:", error);
    }
  };

  const truncateContent = (content: string, maxLength = 50) => {
    if (!content) return "";
    if (content.length <= maxLength) {
      return content;
    }
    return content.substring(0, maxLength) + "...";
  };

  return (
    <div className="min-h-screen">
      {/* Header */}
      <section
        className="flex h-96 max-h-[30rem] w-full items-center justify-center pt-16"
        style={{
          backgroundImage: `url('${forumHeaderImg}')`,
          backgroundPosition: "center",
          backgroundSize: "cover",
        }}
      >
        <div className="max-w-6xl px-4">
          <h1 className="mb-4 text-center text-4xl font-bold sm:text-6xl">
            Pet Care Connect
          </h1>
          <p className="tight mb-8 text-center text-xs text-gray-600 md:text-sm">
            Share, Learn, and Connect with Fellow Pet Owners
          </p>

          {/* Search Bar */}
          <div className="max-w-3xl">
            <div className="flex items-center gap-1 rounded-md bg-white p-2">
              <Search className="h-5 w-5 text-gray-400" />
              <Input
                placeholder="Search everything pet care related..."
                className="border-0 bg-transparent px-1 focus-visible:ring-0 focus-visible:ring-offset-0"
              />
            </div>
          </div>
        </div>
      </section>

      {/* Services */}
      <section className="py-16">
        {/* Categories */}
        <div className="section-width mx-auto grid max-w-6xl grid-cols-2 gap-6 px-6 lg:grid-cols-3">
          {categories.map((category, index) => (
            <Card
              key={index}
              className="cursor-pointer border border-0 p-6 shadow transition duration-300 hover:shadow-lg"
            >
              <div className="flex flex-col items-center gap-4">
                <img src={category.src} alt={category.title} />
                <h3 className="text-center font-medium">{category.title}</h3>
              </div>
            </Card>
          ))}
        </div>
      </section>

      {/* Featured Topics (Now Featured Forums) */}
      <section className="py-16">
        <div className="section-width">
          <h2 className="mb-6 text-2xl font-semibold">Featured Forums</h2>{" "}
          {/* Changed title */}
          <div className="space-y-3">
            {featuredForums.map(
              (
                forum, // Map over featuredForums
              ) => (
                <Link
                  key={forum.forumId}
                  to={`${ROUTES.FORUM}/${forum.forumId}`}
                  className="block"
                >
                  <Card key={forum.forumId} className="border-0 bg-white p-4">
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-2">
                        <Avatar className="h-8 w-8 text-gray-600">
                          <AvatarImage src={forum.userProfile || ""} />
                          <AvatarFallback>
                            {forum.firstName?.charAt(0) || "?"}
                          </AvatarFallback>
                        </Avatar>
                        <p className="text-sm">{forum.title}</p>{" "}
                        {/* Display forum title */}
                      </div>
                      <div className="flex items-center gap-4 text-gray-500">
                        <span className="flex items-center gap-1">
                          <MessageCircle className="h-4 w-4" />{" "}
                          {forum.comments?.length || 0}
                        </span>
                        <span className="flex items-center gap-1">
                          <Eye className="h-4 w-4" /> {forum.views || 0}{" "}
                          {/* Add views if available */}
                        </span>
                      </div>
                    </div>
                  </Card>
                </Link>
              ),
            )}
          </div>
        </div>
      </section>

      {/* Two Column Layout */}
      <section className="py-16">
        <div className="section-width grid gap-8 lg:col-span-2 lg:grid-cols-3">
          {/* Display Forums */}
          <article className="col-span-2">
            <h2 className="mb-4 text-xl font-semibold">Recent activity</h2>
            <div className="space-y-3">
              {forums.map((forum) => (
                <Link
                  key={forum.forumId}
                  to={`${ROUTES.FORUM}/${forum.forumId}`}
                  className="block"
                >
                  <Card key={forum.forumId} className="border-0 bg-white p-4">
                    <div className="flex items-center gap-4">
                      <div className="flex h-8 w-8 items-center justify-center rounded-full bg-blue-100 text-blue-600">
                        <Avatar className="h-8 w-8">
                          <AvatarImage
                            src={forum.userProfile || ""}
                            alt={forum.firstName}
                          />
                          <AvatarFallback>
                            {forum.firstName?.charAt(0) || "?"}
                          </AvatarFallback>
                        </Avatar>
                      </div>
                      <div className="flex-grow">
                        <h3 className="mb-2 text-sm font-semibold">
                          {forum.firstName} {forum.lastName}
                        </h3>
                        <p className="text-sm text-gray-900">{forum.title}</p>
                        <div className="mt-2 flex items-center gap-4 text-sm text-xs text-gray-500">
                          <Button
                            variant="ghost"
                            className="flex w-auto items-center gap-1 p-0 hover:bg-transparent"
                          >
                            <FaRegHeart className="h-4 w-4" />
                            {forum.likes?.length || 0}
                          </Button>
                          <Button
                            variant="ghost"
                            className="flex w-auto items-center gap-1 p-0 hover:bg-transparent"
                          >
                            <FaRegMessage className="h-4 w-4" />
                            {forum.comments?.length || 0}
                          </Button>
                          <span className="flex items-center gap-1">
                            {formatRelativeTime(forum.createdAt)}
                          </span>
                        </div>
                      </div>
                    </div>
                  </Card>
                </Link>
              ))}
            </div>
          </article>

          {/* Solved Topics */}
          <article className="col-span-1">
            <h2 className="mb-4 text-xl font-semibold">Solved topics</h2>
            <div className="space-y-3">
              {solvedTopics.map((topic, index) => (
                <Card
                  key={index}
                  className="cursor-pointer border-0 bg-white p-4"
                >
                  <div className="flex items-start gap-2">
                    <Check className="mt-1 h-4 w-4 flex-shrink-0 text-green-500" />
                    <p className="text-sm">{topic}</p>
                  </div>
                </Card>
              ))}
            </div>
          </article>
        </div>
      </section>

      {/* Two Column Layout */}
      <section className="py-16">
        <div className="section-width grid gap-8 lg:col-span-2 lg:grid-cols-3">
          {/* Display Forums */}
          <article className="col-span-2">
            <h2 className="mb-4 text-xl font-semibold">Recent activity</h2>
            <div className="space-y-3">
              {forums.map((forum) => (
                <Link
                  key={forum.forumId}
                  to={`${ROUTES.FORUM}/${forum.forumId}`}
                  className="block"
                >
                  <Card key={forum.forumId} className="border-0 bg-white p-4">
                    <div className="flex items-center gap-4">
                      <div className="flex h-8 w-8 items-center justify-center rounded-full bg-blue-100 text-blue-600">
                        <Avatar className="h-8 w-8">
                          <AvatarImage
                            src={forum.userProfile || ""}
                            alt={forum.firstName}
                          />
                          <AvatarFallback>
                            {forum.firstName?.charAt(0) || "?"}
                          </AvatarFallback>
                        </Avatar>
                      </div>
                      <div className="flex-grow">
                        <h3 className="mb-2 text-sm font-semibold">
                          {forum.firstName} {forum.lastName}
                        </h3>
                        <p className="text-sm text-gray-900">{forum.title}</p>
                        <p className="mt-1 text-xs text-gray-500">
                          {truncateContent(forum.content)}
                        </p>
                        <div className="mt-2 flex items-center gap-4 text-sm text-xs text-gray-500">
                          <Button
                            variant="ghost"
                            className="flex w-auto items-center gap-1 p-0 hover:bg-transparent"
                          >
                            <FaRegHeart className="h-4 w-4" />{" "}
                            {forum.likes?.length || 0}
                          </Button>
                          <Button
                            variant="ghost"
                            className="flex w-auto items-center gap-1 p-0 hover:bg-transparent"
                          >
                            <FaRegMessage className="h-4 w-4" />{" "}
                            {forum.comments?.length || 0}
                          </Button>
                          <span className="flex items-center gap-1">
                            {formatRelativeTime(forum.createdAt)}
                          </span>
                        </div>
                      </div>
                    </div>
                  </Card>
                </Link>
              ))}
            </div>
          </article>

          {/* Solved Topics */}
          <article className="col-span-1">
            <h2 className="mb-4 text-xl font-semibold">Solved topics</h2>
            <div className="space-y-3">
              {solvedTopics.map((topic, index) => (
                <Card
                  key={index}
                  className="cursor-pointer border-0 bg-white p-4"
                >
                  <div className="flex items-start gap-2">
                    <Check className="mt-1 h-4 w-4 flex-shrink-0 text-green-500" />
                    <p className="text-sm">{topic}</p>
                  </div>
                </Card>
              ))}
            </div>
          </article>
        </div>
      </section>

      {/* Create Forum Section with Input Fields */}
      <section className="py-16">
        <div className="section-width mx-auto px-6">
          <h2 className="mb-4 text-xl font-semibold">Create a New Forum</h2>

          {/* Title Input */}
          <Input
            placeholder="Forum Title"
            value={newForumTitle}
            onChange={(e) => setNewForumTitle(e.target.value)}
            className="mb-4" // Add some margin below
          />

          {/* Tags Input (Basic Example - Improve as needed) */}
          <Input
            placeholder="Tags (comma-separated)"
            value={newForumTags.join(",")} // Display tags as comma-separated
            onChange={(e) => {
              const tags = e.target.value.split(",").map((tag) => tag.trim());
              setNewForumTags(tags);
            }}
            className="mb-4" // Add some margin below
          />

          <div className="mb-4 min-h-[13rem]">
            <ReactQuill
              className="h-full"
              value={newForumContent}
              onChange={setNewForumContent}
              modules={{
                toolbar: [
                  ["bold", "italic"],
                  [{ list: "ordered" }, { list: "bullet" }],
                  ["link"],
                ],
              }}
              formats={["bold", "italic", "list", "bullet", "link"]}
              placeholder="Write your forum content here..."
            />
          </div>
          <Button onClick={handleCreateForum} className="mt-4">
            Create Forum
          </Button>
        </div>
      </section>
    </div>
  );
};

export default ForumPage;
