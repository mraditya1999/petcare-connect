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
import {
  FaHeart,
  FaRegHeart,
  FaRegMessage,
  FaTrash,
  FaPenToSquare,
} from "react-icons/fa6";
import {
  IForum,
  IComment,
  ISingleForumResponse,
  ICommentResponse,
} from "@/types/forum-types";
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
  const [editingCommentId, setEditingCommentId] = useState<string | null>(null);
  const [editText, setEditText] = useState("");

  const COMMENTS_PER_PAGE = 5;
  const user = getUserFromStorage();
  const currentUserId = user?.data?.userId;

  const handleEditComment = (comment: IComment) => {
    setEditingCommentId(comment.commentId);
    setEditText(comment.text);
  };

  const handleSaveEdit = async (commentId: string) => {
    try {
      const response = await customFetch.put(
        `/comments/${commentId}`,
        {
          text: editText,
        },
        {
          headers: {
            "Content-Type": "application/json",
          },
        },
      );

      if (response.data?.data) {
        // Update UI immediately
        setComments((prev) =>
          prev.map((c) =>
            c.commentId === commentId ? { ...c, text: editText } : c,
          ),
        );

        setEditingCommentId(null);
        showToast("Comment updated successfully!", "default");
      } else {
        showToast("Failed to update comment.", "destructive");
      }
    } catch (error) {
      console.error("Error updating comment:", error);
      showToast("Error updating comment.", "destructive");
    }
  };

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

  const fetchForumData = async () => {
    if (!forumId) return;
    try {
      const forumResponse = await customFetch.get<ISingleForumResponse>(
        `/forums/${forumId}`,
      );
      const forumData = forumResponse.data;
      setForum(forumData.data);

      // fetch comments for the current page (commentPage)
      await fetchForumComments(forumId, commentPage);
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

  useEffect(() => {
    if (error) {
      showToast(error, "destructive");
    }
  }, [error]);

  /* effects */
  useEffect(() => {
    fetchForumData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [forumId]);

  useEffect(() => {
    if (forumId) {
      fetchForumComments(forumId, commentPage);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [commentPage, forumId]);

  const handleDeleteComment = async (commentId: string) => {
    try {
      await customFetch.delete(`/comments/${commentId}`);

      const updatedCount = Math.max((forum?.commentsCount || 1) - 1, 0);
      const newTotalPages = Math.max(
        Math.ceil(updatedCount / COMMENTS_PER_PAGE),
        1,
      );

      // Update forum count
      setForum((prev) =>
        prev ? { ...prev, commentsCount: updatedCount } : prev,
      );

      // Move to previous page if needed
      let finalPage = commentPage;
      if (commentPage > newTotalPages - 1) {
        finalPage = newTotalPages - 1;
        setCommentPage(finalPage);
      }

      // 🔥 MOST IMPORTANT PART → FETCH UPDATED PAGE DATA
      await fetchForumComments(forumId!, finalPage);

      showToast("Comment deleted successfully", "default");
    } catch (error) {
      console.error(error);
      showToast("Failed to delete comment", "destructive");
    }
  };

  const handleAddComment = async () => {
    if (newComment.trim() === "") return;
    setAddingComment(true);

    try {
      const response = await customFetch.post<ICommentResponse>(
        `/comments/forums/${forumId}`,
        { text: newComment },
      );

      if (response.data?.data) {
        const newAddedComment = response.data.data;

        // Derive the updated count reliably: prefer forum?.commentsCount if available else comments.length
        const priorCount = (forum?.commentsCount ?? comments.length) || 0;
        const updatedCount = priorCount + 1;
        const newTotalPages = Math.max(
          Math.ceil(updatedCount / COMMENTS_PER_PAGE),
          1,
        );

        // Update forum count atomically
        setForum((prev) =>
          prev
            ? { ...prev, commentsCount: (prev.commentsCount || 0) + 1 }
            : prev,
        );

        // Update total pages
        setTotalCommentPages(newTotalPages);

        // If user is on first page (page 0), show the new comment there (prepend)
        // Trim to COMMENTS_PER_PAGE to avoid letting page contain > size
        if (commentPage === 0) {
          setComments((prev) =>
            [newAddedComment, ...prev].slice(0, COMMENTS_PER_PAGE),
          );
        } else {
          // If user is on another page, do not mutate that page — it will reflect count when they navigate.
          // Optionally you could navigate user to page 0; we keep current behaviour.
        }

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

  if (loading) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <LoadingSpinner />
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <p className="text-red-600">Something went wrong. Please try again.</p>
      </div>
    );
  }

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
          <h2 className="text-lg font-semibold">Comments</h2>
          {comments.length > 0 ? (
            comments.map((comment) => (
              <div
                key={comment.commentId}
                className="mb-3 flex justify-between rounded-xl border border-gray-100 bg-white p-4 shadow"
              >
                <div className="flex w-full items-start space-x-3">
                  <Avatar className="h-10 w-10">
                    <AvatarImage
                      src={comment?.firstName || ""}
                      alt="User Avatar"
                    />
                    <AvatarFallback>
                      {comment?.firstName?.slice(0, 1) || "?"}
                    </AvatarFallback>
                  </Avatar>

                  <div className="w-full">
                    <p className="text-sm font-semibold">
                      {`${comment?.firstName || ""} ${comment?.lastName || ""}`}
                    </p>

                    {editingCommentId === comment.commentId ? (
                      <div className="mt-2 w-full">
                        <textarea
                          className="w-full rounded-md border p-2 text-sm focus:outline-none focus:ring focus:ring-blue-200"
                          rows={3}
                          value={editText}
                          onChange={(e) => setEditText(e.target.value)}
                          placeholder="Edit your comment..."
                        ></textarea>

                        <div className="mt-3 flex gap-2">
                          <Button
                            size="sm"
                            onClick={() => handleSaveEdit(comment.commentId)}
                          >
                            Save
                          </Button>

                          <Button
                            size="sm"
                            variant="outline"
                            onClick={() => setEditingCommentId(null)}
                          >
                            Cancel
                          </Button>
                        </div>
                      </div>
                    ) : (
                      <div
                        className="prose mt-1 max-w-none text-sm"
                        dangerouslySetInnerHTML={{ __html: comment.text }}
                      />
                    )}

                    <p className="mt-1 text-xs text-gray-500">
                      {comment?.createdAt
                        ? formatRelativeTime(comment.createdAt)
                        : "Just now"}
                    </p>
                  </div>
                </div>

                {comment.userId === currentUserId &&
                  editingCommentId !== comment.commentId && (
                    <div className="flex flex-col items-center gap-3">
                      <button
                        className="text-gray-600 transition hover:text-blue-600"
                        onClick={() => handleEditComment(comment)}
                      >
                        <FaPenToSquare />
                      </button>

                      <button
                        className="text-gray-600 transition hover:text-red-600"
                        onClick={() => handleDeleteComment(comment.commentId)}
                      >
                        <FaTrash />
                      </button>
                    </div>
                  )}
              </div>
            ))
          ) : (
            <p className="text-gray-500">No comments yet.</p>
          )}
          {totalCommentPages > 1 && (
            <PaginationControl
              currentPage={commentPage}
              totalPages={totalCommentPages}
              onPageChange={setCommentPage}
            />
          )}
        </div>

        {user && (
          <div className="mt-4 rounded-lg bg-white p-6 shadow-md">
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
