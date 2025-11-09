import { useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import { customFetch } from "@/utils/customFetch";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import {
  formatRelativeTime,
  getUserFromStorage,
  showToast,
} from "@/utils/helpers";
import { Button } from "@/components/ui/button";
import { FaHeart, FaRegHeart, FaRegMessage } from "react-icons/fa6";
import { IForum, IComment, ISingleForumResponse } from "@/types/forum-types";
import { LoadingSpinner } from "@/components/ui/LoadingSpinner";
import PaginationControl from "@/components/forum/PaginationControl";

const SingleForumPage = () => {
  const { forumId } = useParams<{ forumId: string }>();

  const [forum, setForum] = useState<IForum | null>(null);
  const [comments, setComments] = useState<IComment[]>([]); // ✅ separate state
  const [newComment, setNewComment] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [addingComment, setAddingComment] = useState(false);
  const [showCommentBox, setShowCommentBox] = useState(false);
  const [liked, setLiked] = useState(false);
  const [likeProcessing, setLikeProcessing] = useState(false);
  const [commentPage, setCommentPage] = useState(0);
  const [totalCommentPages, setTotalCommentPages] = useState(1);

  const COMMENTS_PER_PAGE = 5;
  const user = getUserFromStorage();

  /* -------------------------------------------
   ✅ Fetch comments (separate and reusable)
-------------------------------------------- */
  const fetchForumComments = async (forumId: string, page: number) => {
    try {
      const response = await customFetch(
        `/comments/forums/${forumId}?page=${page}&size=${COMMENTS_PER_PAGE}`,
      );
      const commentsData = response.data.data?.content || [];
      const totalPages = response.data.data?.page?.totalPages || 1;

      setComments(commentsData);
      setTotalCommentPages(totalPages);
    } catch (err) {
      console.error("Error fetching comments:", err);
      showToast("Error fetching comments. Please try again.", "destructive");
    }
  };

  /* -------------------------------------------
   ✅ Fetch forum details
-------------------------------------------- */
  const fetchForumData = async () => {
    if (!forumId) return;
    try {
      const forumResponse = await customFetch.get<ISingleForumResponse>(
        `/forums/${forumId}`,
      );
      const forumData = forumResponse.data;
      setForum(forumData.data);

      await fetchForumComments(forumId, commentPage); // ✅ call comment fetch
      if (user) {
        const checkLikeResponse = await customFetch(
          `/likes/forums/${forumId}/check`,
        );
        const isLiked = checkLikeResponse.data.data?.isLiked || false;
        setLiked(isLiked);
      }
    } catch (err) {
      setError("Error fetching forum details. Please try again.");
      showToast(
        "Error fetching forum details. Please try again.",
        "destructive",
      );
      console.error("Error fetching forum details:", err);
    } finally {
      setLoading(false);
    }
  };

  /* -------------------------------------------
   ✅ Effects for data loading and pagination
-------------------------------------------- */
  useEffect(() => {
    fetchForumData();
  }, [forumId]);

  useEffect(() => {
    if (forumId) {
      fetchForumComments(forumId, commentPage);
    }
  }, [commentPage, forumId]);

  const handleLike = async () => {
    if (likeProcessing) return;
    setLikeProcessing(true);

    try {
      const response = await customFetch.post<ILikeResponse>(
        `/likes/forums/${forumId}`,
      );

      const message = response.data.message.toLowerCase();
      const user = getUserFromStorage();
      const currentUserId = user?.data?.userId;

      if (!currentUserId) {
        showToast("Please log in to like this post.", "destructive");
        return;
      }

      const isUnlike = message.includes("unlike");
      const isLike = message.includes("like");

      setForum((prev) => {
        if (!prev) return prev;
        const existingLikes = prev.likes || [];

        if (isUnlike) {
          setLiked(false);
          return {
            ...prev,
            likes: existingLikes.filter((l) => l.userId !== currentUserId),
            likesCount: Math.max(
              (prev.likesCount || existingLikes.length) - 1,
              0,
            ),
          };
        }

        if (isLike && !existingLikes.some((l) => l.userId === currentUserId)) {
          setLiked(true);
          showToast("You liked this forum post.", "default");
          return {
            ...prev,
            likes: [
              ...existingLikes,
              {
                likeId: crypto.randomUUID(),
                forumId: forumId!,
                userId: currentUserId,
                createdAt: new Date().toISOString(),
              },
            ],
            likesCount: (prev.likesCount || existingLikes.length) + 1,
          };
        }

        return prev;
      });
    } catch (err) {
      console.error("Error toggling like:", err);
      showToast("Please log in to like this forum post.", "destructive");
    } finally {
      setLikeProcessing(false);
    }
  };

  // const handleLike = async () => {
  //   if (likeProcessing) return;
  //   setLikeProcessing(true);

  //   try {
  //     const response = await customFetch.post<ILikeResponse>(
  //       `/likes/forums/${forumId}`,
  //     );

  //     const message = response.data.message.toLowerCase();
  //     const user = getUserFromStorage();

  //     const currentUserId = user?.data?.userId;

  //     if (!currentUserId) {
  //       showToast("Please log in to like this post.", "destructive");
  //       return;
  //     }

  //     setForum((prev) => {
  //       if (!prev) return prev;
  //       const existingLikes = prev.likes || [];

  //       // ✅ Detect unlike action
  //       if (message.includes("unlike")) {
  //         const updatedLikes = existingLikes.filter(
  //           (l) => l.userId !== currentUserId,
  //         );
  //         setLiked(false);
  //         return {
  //           ...prev,
  //           likes: updatedLikes,
  //           likesCount: (prev.likesCount || existingLikes.length) - 1,
  //         };
  //       }

  //       // ✅ Detect like action
  //       if (message.includes("like")) {
  //         const alreadyLiked = existingLikes.some(
  //           (l) => l.userId === currentUserId,
  //         );
  //         if (!alreadyLiked) {
  //           setLiked(true);
  //           return {
  //             ...prev,
  //             likes: [
  //               ...existingLikes,
  //               {
  //                 likeId: crypto.randomUUID(),
  //                 forumId: forumId!,
  //                 userId: currentUserId,
  //                 createdAt: new Date().toISOString(),
  //               },
  //             ],
  //             likesCount: (prev.likesCount || existingLikes.length) + 1,
  //           };
  //         }
  //       }

  //       return prev;
  //     });
  //   } catch (err) {
  //     console.error("Error toggling like:", err);
  //     showToast("Please log in to like this forum post.", "destructive");
  //   } finally {
  //     setLikeProcessing(false);
  //   }
  // };

  const handleAddComment = async () => {
    if (newComment.trim() === "") return;
    setAddingComment(true);
    try {
      const response = await customFetch.post<ICommentResponse>(
        `/comments/forums/${forumId}`,
        {
          text: newComment,
        },
      );

      if (response.data?.data) {
        setForum((prevForum) => {
          if (!prevForum) return prevForum;

          const updatedComments = [
            ...(prevForum.comments || []),
            response.data.data,
          ];

          return {
            ...prevForum,
            comments: updatedComments,
            commentsCount: (prevForum.commentsCount || 0) + 1,
          };
        });
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
    return (
      <div className="flex min-h-screen items-center justify-center">
        <LoadingSpinner />
      </div>
    );
  if (error) return showToast(error, "destructive");

  return (
    <section className="py-16">
      <div className="section-width mx-auto mt-6 space-y-6 rounded-lg p-4 shadow-lg">
        <div className="flex items-center space-x-3 rounded-t-lg bg-gray-50 p-6">
          <Avatar className="h-10 w-10">
            <AvatarImage src={forum?.firstName || ""} alt="User Avatar" />
            <AvatarFallback>
              {forum?.firstName?.slice(0, 1) || "?"}
            </AvatarFallback>{" "}
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
            disabled={!user || likeProcessing}
            title={!user ? "Log in to like this forum" : undefined}
          >
            {liked ? (
              <FaHeart className="h-4 w-4 text-red-500 transition-colors duration-200" />
            ) : (
              <FaRegHeart className="h-4 w-4 text-gray-500 transition-colors duration-200" />
            )}
            <span className="ml-1">{forum?.likesCount || 0}</span>
          </Button>

          <Button
            variant="ghost"
            className="flex items-center gap-1 p-0 hover:bg-transparent"
            onClick={() => setShowCommentBox(!showCommentBox)}
            disabled={!user || likeProcessing}
            title={!user ? "Log in to comment this forum" : undefined}
          >
            {forum?.commentsCount || 0} <FaRegMessage className="h-4 w-4" />{" "}
            {/* Display comments count */}
          </Button>
        </div>
        {forum && forum.tags && forum.tags.length > 0 && (
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
          {forum && forum.commentsCount > 0 ? (
            comments.map((comment) => (
              <div className="mb-2 flex items-center space-x-3 rounded-t-lg bg-gray-50 p-6">
                <Avatar className="h-10 w-10">
                  <AvatarImage
                    src={comment?.firstName || ""}
                    alt="User Avatar"
                  />
                  <AvatarFallback>
                    {comment?.firstName?.slice(0, 1) || "?"}
                  </AvatarFallback>
                </Avatar>
                <div>
                  <p className="text-sm font-semibold">{`${comment?.firstName || ""} ${comment?.lastName || ""}`}</p>
                  <p className="text-sm">{comment.text}</p>
                  <p className="text-xs text-gray-500">
                    {comment?.createdAt
                      ? formatRelativeTime(comment.createdAt)
                      : "Just now"}
                  </p>
                </div>
              </div>
            ))
          ) : (
            <p className="text-gray-500">No comments yet.</p>
          )}
          <PaginationControl
            currentPage={commentPage}
            totalPages={totalCommentPages}
            onPageChange={setCommentPage}
          />
        </div>

        {user && (
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
        )}
      </div>
    </section>
  );
};

export default SingleForumPage;
