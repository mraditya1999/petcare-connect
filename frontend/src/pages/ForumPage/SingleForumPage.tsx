// import { useParams } from "react-router-dom";
// import { useEffect, useState } from "react";
// import { customFetch } from "@/utils/customFetch";
// import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
// import { formatRelativeTime, showToast } from "@/utils/helpers";
// import { Button } from "@/components/ui/button";
// import { FaRegHeart, FaRegMessage } from "react-icons/fa6";

// // Define the types
// interface Comment {
//   commentId: string;
//   forumId: string;
//   userId: string;
//   text: string;
//   createdAt: string;
// }

// interface Like {
//   likeId: string;
//   forumId: string;
//   userId: string;
//   createdAt: string;
// }

// interface Forum {
//   forumId: string;
//   userId: string;
//   firstName: string;
//   lastName: string;
//   email: string;
//   title: string;
//   content: string;
//   createdAt: string;
//   updatedAt: string;
//   comments: Comment;
//   likes: Like;
//   tags: string;
// }

// const SingleForumPage = () => {
//   const { forumId } = useParams<{ forumId: string }>();
//   const [forum, setForum] = useState<Forum | null>(null);
//   const [newComment, setNewComment] = useState("");
//   const [loading, setLoading] = useState(true);
//   const [error, setError] = useState<string | null>(null);
//   const [addingComment, setAddingComment] = useState(false);
//   const [showCommentBox, setShowCommentBox] = useState(false);
//   const [liked, setLiked] = useState(false);

//   useEffect(() => {
//     const fetchForumData = async () => {
//       try {
//         const forumResponse = await customFetch(`/forums/${forumId}`);
//         const forumData = forumResponse.data.data as Forum;
//         setForum(forumData);

//         // const likeResponse = await customFetch(`/forums/${forumId}/checklike`);
//         // if (likeResponse.data) {
//         //   setLiked(likeResponse.data.liked);
//         // }
//       } catch (err) {
//         setError("Error fetching forum details or likes. Please try again.");
//         showToast(
//           "Error fetching forum details or likes. Please try again.",
//           "destructive",
//         );
//         console.error("Error fetching forum details:", err);
//       } finally {
//         setLoading(false);
//       }
//     };

//     fetchForumData();
//   }, [forumId]);

//   const handleLike = async () => {
//     try {
//       const response = await customFetch.post(`/forums/${forumId}/like`);
//       if (response.data) {
//         setLiked(response.data.liked);
//         setForum((prevForum) => ({ ...prevForum, likes: response.data.likes })); // Update likes in forum state
//       }
//     } catch (error) {
//       console.error("Error liking forum:", error);
//       showToast("Error liking forum. Please try again.", "destructive");
//     }
//   };

//   const handleAddComment = async () => {
//     if (newComment.trim() === "") return;
//     setAddingComment(true);
//     try {
//       const response = await customFetch.post(`/forums/${forumId}/comment`, {
//         text: newComment,
//         // userId: currentUserId // If required by your backend, and if you have currentUserId available
//       });

//       if (response.data) {
//         setForum((prevForum) => ({
//           ...prevForum!,
//           comments: [
//             ...(prevForum!.comments || []), // Correct spread syntax
//             response.data.data, // Assuming your backend returns the new comment object
//           ],
//         }));
//         setNewComment("");
//         showToast("Comment added successfully!", "default");
//       } else {
//         showToast("Failed to add comment. Please try again.", "destructive");
//       }
//     } catch (error) {
//       console.error("Error adding comment:", error);
//       showToast("Error adding comment. Please try again.", "destructive");
//     } finally {
//       setAddingComment(false);
//     }
//   };

//   if (loading)
//     return <p className="text-center text-gray-500">Loading forum...</p>;
//   if (error) return <p className="text-center text-red-500">{error}</p>;

//   return (
//     <section className="py-16">
//       <div className="section-width mx-auto mt-6 space-y-6 rounded-lg p-4 shadow-lg">
// <div className="flex items-center space-x-3 rounded-t-lg bg-gray-50 p-6">
//   <Avatar className="h-10 w-10">
//     <AvatarImage src={forum?.userProfile || ""} alt="User Avatar" />
//     <AvatarFallback>
//       {forum?.firstName?.slice(0, 1) || "?"}
//     </AvatarFallback>{" "}
//     {/* Handle null firstName */}
//   </Avatar>
//   <div>
//     <p className="text-sm font-semibold">{`${forum?.firstName || ""} ${forum?.lastName || ""}`}</p>{" "}
//     {/* Handle null names */}
//     <p className="text-xs text-gray-500">
//       {forum?.createdAt
//         ? formatRelativeTime(forum.createdAt)
//         : "Just now"}
//     </p>
//   </div>
// </div>
// <h1 className="text-3xl font-bold">{forum?.title}</h1>
// <div
//   className="text-lg text-gray-700"
//   dangerouslySetInnerHTML={{ __html: forum?.content || "" }}
// ></div>

//         <div className="mt-4 flex items-center justify-end gap-4">
//           <Button
//             variant="ghost"
//             className="flex items-center gap-1 p-0 hover:bg-transparent"
//             onClick={handleLike}
//           >
//             <FaRegHeart />
//             {forum?.likes?.length || 0}
//           </Button>
//           <Button
//             variant="ghost"
//             className="flex items-center gap-1 p-0 hover:bg-transparent"
//             onClick={() => setShowCommentBox(!showCommentBox)}
//           >
//             {forum?.comments?.length || 0} <FaRegMessage className="h-4 w-4" />
//           </Button>
//         </div>

//         {forum?.tags?.length > 0 && ( // Optional chaining for tags
//           <div className="mt-4 flex flex-wrap gap-2">
//             {" "}
//             {/* Added margin top */}
//             {forum.tags.map((tag) => (
//               <span
//                 key={tag}
//                 className="rounded-md bg-gray-200 px-2 py-1 text-xs"
//               >
//                 #{tag}
//               </span>
//             ))}
//           </div>
//         )}

//         <div className="mt-4 rounded-lg bg-white p-6 shadow-md">
//           {/* Added margin top */}
//           <h2 className="text-lg font-semibold">Comments</h2>
//           {forum?.comments?.length > 0 ? ( // Optional chaining for comments
//             forum.comments.map((comment, index) => (
//               <div key={index} className="mt-2 border-t p-2">
//                 <p className="text-sm">{comment.text}</p>
//               </div>
//             ))
//           ) : (
//             <p className="text-gray-500">No comments yet.</p>
//           )}
//         </div>

//         {showCommentBox && (
//           <div className="mt-4 rounded-lg bg-white p-6 shadow-md">
//             {" "}
//             {/* Added margin top */}
//             <h2 className="mb-2 text-lg font-semibold">Add a Comment</h2>
//             <textarea
//               className="mb-4 w-full rounded-md border p-2 text-sm"
//               rows={3}
//               placeholder="Add a comment..."
//               value={newComment}
//               onChange={(e) => setNewComment(e.target.value)}
//             ></textarea>
//             <Button onClick={handleAddComment} disabled={addingComment}>
//               {addingComment ? "Adding..." : "Add Comment"}
//             </Button>
//           </div>
//         )}
//       </div>
//     </section>
//   );
// };

// export default SingleForumPage;

import { useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import { customFetch } from "@/utils/customFetch";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { formatRelativeTime, showToast } from "@/utils/helpers";
import { Button } from "@/components/ui/button";
import { FaRegHeart, FaRegMessage } from "react-icons/fa6";

interface Comment {
  commentId: string;
  forumId: string;
  userId: string;
  text: string;
  createdAt: string;
}

interface Like {
  likeId: string;
  forumId: string;
  userId: string;
  createdAt: string;
}

interface Forum {
  forumId: string;
  userId: string;
  firstName: string;
  lastName: string;
  email: string;
  title: string;
  content: string;
  createdAt: string;
  updatedAt: string;
  comments: Comment[]; // Comments should be an array
  likes: Like[]; // Likes should be an array
  tags: string[]; // Tags should be an array
}

interface LikeResponse {
  message: string;
  data: Like;
}

interface CommentResponse {
  message: string;
  data: Comment;
}

const SingleForumPage = () => {
  const { forumId } = useParams<{ forumId: string }>();
  const [forum, setForum] = useState<Forum | null>(null);
  const [newComment, setNewComment] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [addingComment, setAddingComment] = useState(false);
  const [showCommentBox, setShowCommentBox] = useState(false);
  const [liked, setLiked] = useState(false);

  useEffect(() => {
    const fetchForumData = async () => {
      try {
        const forumResponse = await customFetch(`/forums/${forumId}`);
        const forumData = forumResponse.data.data as Forum;
        setForum(forumData);

        // const likeResponse = await customFetch(`/forums/${forumId}/checklike`);
        // if (likeResponse.data) {
        //   setLiked(likeResponse.data.liked);
        // }
      } catch (err) {
        setError("Error fetching forum details or likes. Please try again.");
        showToast(
          "Error fetching forum details or likes. Please try again.",
          "destructive",
        );
        console.error("Error fetching forum details:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchForumData();
  }, [forumId]);

  const handleLike = async () => {
    try {
      const response = await customFetch.post<LikeResponse>(
        `/forums/${forumId}/like`,
      ); // Type the response
      if (response.data) {
        setLiked(response.data.data.likeId !== undefined); // Update liked based on response
        setForum((prevForum) => ({
          ...prevForum!,
          likes: [...(prevForum?.likes || []), response.data.data],
        })); // Add the new like to the array
      }
    } catch (error) {
      console.error("Error liking forum:", error);
      showToast("Error liking forum. Please try again.", "destructive");
    }
  };

  const handleAddComment = async () => {
    if (newComment.trim() === "") return;
    setAddingComment(true);
    try {
      const response = await customFetch.post<CommentResponse>(
        `/forums/${forumId}/comment`,
        {
          // Type the response
          text: newComment,
        },
      );

      if (response.data) {
        setForum((prevForum) => ({
          ...prevForum!,
          comments: [...(prevForum!.comments || []), response.data.data],
        }));
        setNewComment("");
        showToast("Comment added successfully!", "default");
      } else {
        showToast("Failed to add comment. Please try again.", "destructive");
      }
    } catch (error) {
      console.error("Error adding comment:", error);
      showToast("Error adding comment. Please try again.", "destructive");
    } finally {
      setAddingComment(false);
    }
  };

  if (loading)
    return <p className="text-center text-gray-500">Loading forum...</p>;
  if (error) return <p className="text-center text-red-500">{error}</p>;

  return (
    <section className="py-16">
      <div className="section-width mx-auto mt-6 space-y-6 rounded-lg p-4 shadow-lg">
        <div className="flex items-center space-x-3 rounded-t-lg bg-gray-50 p-6">
          <Avatar className="h-10 w-10">
            <AvatarImage src={forum?.userProfile || ""} alt="User Avatar" />
            <AvatarFallback>
              {forum?.firstName?.slice(0, 1) || "?"}
            </AvatarFallback>{" "}
            {/* Handle null firstName */}
          </Avatar>
          <div>
            <p className="text-sm font-semibold">{`${forum?.firstName || ""} ${forum?.lastName || ""}`}</p>{" "}
            {/* Handle null names */}
            <p className="text-xs text-gray-500">
              {forum?.createdAt
                ? formatRelativeTime(forum.createdAt)
                : "Just now"}
            </p>
          </div>
        </div>
        <h1 className="text-3xl font-bold">{forum?.title}</h1>
        <div
          className="text-lg text-gray-700"
          dangerouslySetInnerHTML={{ __html: forum?.content || "" }}
        ></div>

        <div className="mt-4 flex items-center justify-end gap-4">
          <Button
            variant="ghost"
            className="flex items-center gap-1 p-0 hover:bg-transparent"
            onClick={handleLike}
          >
            <FaRegHeart
              className={`h-4 w-4 ${liked ? "text-red-500" : "text-gray-500"}`}
            />
            {forum?.likes?.length || 0} {/* Display likes count */}
          </Button>
          <Button
            variant="ghost"
            className="flex items-center gap-1 p-0 hover:bg-transparent"
            onClick={() => setShowCommentBox(!showCommentBox)}
          >
            {forum?.comments?.length || 0} <FaRegMessage className="h-4 w-4" />{" "}
            {/* Display comments count */}
          </Button>
        </div>
        {forum &&
          forum.tags &&
          forum.tags.length > 0 && ( // Check if forum and forum.tags exist
            <div className="mt-4 flex flex-wrap gap-2">
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
        <div className="mt-4 rounded-lg bg-white p-6 shadow-md">
          {/* Added margin top */}
          <h2 className="text-lg font-semibold">Comments</h2>
          {forum?.comments?.length > 0 ? (
            forum.comments.map((comment) => (
              <div key={comment.commentId} className="mt-2 border-t p-2">
                {/* Use commentId as key */}
                <p className="text-sm">{comment.text}</p>
              </div>
            ))
          ) : (
            <p className="text-gray-500">No comments yet.</p>
          )}
        </div>

        <div className="mt-4 rounded-lg bg-white p-6 shadow-md">
          {/* Added margin top */}
          <h2 className="mb-2 text-lg font-semibold">Add a Comment</h2>
          <textarea
            className="mb-4 w-full rounded-md border p-2 text-sm"
            rows={3}
            placeholder="Add a comment..."
            value={newComment}
            onChange={(e) => setNewComment(e.target.value)}
          ></textarea>
          <Button onClick={handleAddComment} disabled={addingComment}>
            {addingComment ? "Adding..." : "Add Comment"}
          </Button>
        </div>
      </div>
    </section>
  );
};

export default SingleForumPage;
