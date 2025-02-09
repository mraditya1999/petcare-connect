// import { useEffect, useState } from "react";
// import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
// import { Card } from "@/components/ui/card";
// import {  MessageCircle, ThumbsUp } from "lucide-react";
// import { customFetch } from "@/utils/customFetch";
// import { formatDistanceToNow } from "date-fns";
// import { Link } from "react-router-dom";
// import { ROUTES } from "@/utils/constants";

// interface Forum {
//   forumId: string;
//   firstName: string;
//   lastName: string;
//   email: string;
//   title: string;
//   content: string;
//   createdAt: string | null;
//   updatedAt: string | null;
//   likes: { userId: string }[];
//   comments: { userId: string; text: string }[];
//   tags: string[];
//   userId: string;
//   userProfile?: string | null;
// }

// export default function ForumsTab() {
//   const [forums, setForums] = useState<Forum[]>([]);
//   const [loading, setLoading] = useState<boolean>(true);
//   const [error, setError] = useState<string | null>(null);

//   useEffect(() => {
//     const fetchForums = async () => {
//       try {
//         const response = await customFetch("/forums");
//         const data = await response.data;
//         console.log(data);

//         setForums(data);
//       } catch (error) {
//         setError("Error fetching forums. Please try again.");
//         console.error(error);
//       } finally {
//         setLoading(false);
//       }
//     };

//     fetchForums();
//   }, []);

//   if (loading)
//     return <p className="text-center text-gray-500">Loading forums...</p>;
//   if (error) return <p className="text-center text-red-500">{error}</p>;

//   return (
//     <div className="mt-6 space-y-4">
//       {forums.length > 0 ? (
//         forums.map((forum) => (
//           <Card key={forum.forumId} className="rounded-lg border p-4 shadow-sm">
//             <div className="flex items-center space-x-3">
//               <Avatar className="h-8 w-8">
//                 <AvatarImage src={forum.userProfile || ""} alt="User Avatar" />
//                 <AvatarFallback>
//                   {forum.firstName.slice(0, 1)}
//                 </AvatarFallback>{" "}
//                 {/* Fallback if no image */}
//               </Avatar>
//               <div>
//                 <p className="text-sm font-semibold">{`${forum.firstName} ${forum.lastName}`}</p>
//                 <p className="text-xs text-gray-500">
//                   {forum.createdAt
//                     ? formatDistanceToNow(new Date(forum.createdAt), {
//                         addSuffix: true,
//                       })
//                     : "Just now"}
//                 </p>
//               </div>
//             </div>
//             {/* <h3 className="mt-2 text-lg font-semibold">{forum.title}</h3> */}
//             <Link
//               to={`${ROUTES.FORUM}/${forum.forumId}`}
//               className="mt-2 text-lg font-semibold text-blue-600 hover:underline"
//             >
//               {forum.title}
//             </Link>
//             <p className="mt-1 text-sm text-gray-600">{forum.content}</p>

//             {/* Display tags */}
//             {forum.tags.length > 0 && (
//               <div className="mt-2 flex flex-wrap gap-2">
//                 {forum.tags.map((tag) => (
//                   <span
//                     key={tag}
//                     className="rounded-md bg-gray-200 px-2 py-1 text-xs"
//                   >
//                     #{tag}
//                   </span>
//                 ))}
//               </div>
//             )}

//             {/* Like, Views, and Comments */}
//             <div className="mt-3 flex items-center space-x-4 text-sm text-gray-500">
//               <div className="flex items-center space-x-1">
//                 <ThumbsUp className="h-4 w-4" />
//                 <span>{forum.likes?.length || 0}</span>
//               </div>
//               <div className="flex items-center space-x-1">
//                 <MessageCircle className="h-4 w-4" />
//                 <span>{forum.comments?.length || 0}</span>
//               </div>
//             </div>
//           </Card>
//         ))
//       ) : (
//         <p className="text-center text-gray-500">No forums uploaded yet.</p>
//       )}
//     </div>
//   );
// }

import { useEffect, useState } from "react";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Card } from "@/components/ui/card";
import { MessageCircle, ThumbsUp } from "lucide-react";
import { customFetch } from "@/utils/customFetch";
import { formatDistanceToNow } from "date-fns";
import { Link } from "react-router-dom";
import { ROUTES } from "@/utils/constants";

interface Forum {
  forumId: string;
  firstName: string;
  lastName: string;
  email: string;
  title: string;
  content: string;
  createdAt: string | null;
  updatedAt: string | null;
  likes: { userId: string }[];
  comments: { userId: string; text: string }[];
  tags: string[] | null; // Allow tags to be null
  userId: string;
  userProfile?: string | null;
}

export default function ForumsTab() {
  const [forums, setForums] = useState<Forum[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchForums = async () => {
      try {
        const response = await customFetch("/forums/my-forums");
        const data = await response.data;
        console.log(data);

        setForums(data);
      } catch (error) {
        setError("Error fetching forums. Please try again.");
        console.error(error);
      } finally {
        setLoading(false);
      }
    };

    fetchForums();
  }, []);

  if (loading)
    return <p className="text-center text-gray-500">Loading forums...</p>;
  if (error) return <p className="text-center text-red-500">{error}</p>;

  return (
    <div className="mt-6 space-y-4">
      {forums.length > 0 ? (
        forums.map((forum) => (
          <Card key={forum.forumId} className="rounded-lg border p-4 shadow-sm">
            <div className="flex items-center space-x-3">
              <Avatar className="h-8 w-8">
                <AvatarImage src={forum.userProfile || ""} alt="User Avatar" />
                <AvatarFallback>
                  {forum.firstName.slice(0, 1)}
                </AvatarFallback>{" "}
                {/* Fallback if no image */}
              </Avatar>
              <div>
                <p className="text-sm font-semibold">{`${forum.firstName} ${forum.lastName}`}</p>
                <p className="text-xs text-gray-500">
                  {forum.createdAt
                    ? formatDistanceToNow(new Date(forum.createdAt), {
                        addSuffix: true,
                      })
                    : "Just now"}
                </p>
              </div>
            </div>
            <Link
              to={`${ROUTES.FORUM}/${forum.forumId}`}
              className="mt-2 text-lg font-semibold text-blue-600 hover:underline"
            >
              {/* Display title with HTML formatting */}
              <span dangerouslySetInnerHTML={{ __html: forum.title }} />
            </Link>
            {/* Display content with HTML formatting */}
            <p
              className="mt-1 text-sm text-gray-600"
              dangerouslySetInnerHTML={{ __html: forum.content }}
            ></p>

            {/* Display tags */}
            {forum.tags && forum.tags.length > 0 && (
              <div className="mt-2 flex flex-wrap gap-2">
                {forum.tags.map((tag) => (
                  <span
                    key={tag}
                    className="rounded-md bg-gray-200 px-2 py-1 text-xs"
                  >
                    #{tag}
                  </span>
                ))}
              </div>
            )}

            {/* Like, Views, and Comments */}
            <div className="mt-3 flex items-center space-x-4 text-sm text-gray-500">
              <div className="flex items-center space-x-1">
                <ThumbsUp className="h-4 w-4" />
                <span>{forum.likes?.length || 0}</span>
              </div>
              <div className="flex items-center space-x-1">
                <MessageCircle className="h-4 w-4" />
                <span>{forum.comments?.length || 0}</span>
              </div>
            </div>
          </Card>
        ))
      ) : (
        <p className="text-center text-gray-500">No forums uploaded yet.</p>
      )}
    </div>
  );
}
