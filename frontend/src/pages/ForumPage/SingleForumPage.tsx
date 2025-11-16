import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import {
  FaHeart,
  FaRegHeart,
  FaRegMessage,
  FaTrash,
  FaPenToSquare,
} from "react-icons/fa6";
import { LoadingSpinner } from "@/components/ui/LoadingSpinner";
import PaginationControl from "@/components/forum/PaginationControl";
import {
  fetchSingleForum,
  fetchComments,
  createComment,
  updateComment,
  deleteComment,
  toggleLike,
  checkLike,
} from "@/features/forum/forumDetailThunk";
import { useAppDispatch, useAppSelector } from "@/app/hooks";
import {
  getUserFromStorage,
  showToast,
  formatRelativeTime,
} from "@/utils/helpers";
import { IComment } from "@/types/forum-types";
import {
  setCommentPage,
  selectIsLiked,
} from "@/features/forum/forumDetailSlice";

const SingleForumPage = () => {
  const { forumId } = useParams<{ forumId: string }>();
  const dispatch = useAppDispatch();
  const {
    forum,
    comments,
    commentPage,
    totalCommentPages,
    loading,
    error,
    likeProcessing,
  } = useAppSelector((state) => state.forumDetail);

  const [newComment, setNewComment] = useState("");
  const [addingComment, setAddingComment] = useState(false);
  const [editingCommentId, setEditingCommentId] = useState<string | null>(null);
  const [editText, setEditText] = useState("");
  const [showCommentBox, setShowCommentBox] = useState(false);

  const user = getUserFromStorage();
  const currentUserId = user?.data?.userId;
  const COMMENTS_PER_PAGE = 5;
  const isLiked = useAppSelector(selectIsLiked);
  // Initial fetch of forum + comments + like-check
  useEffect(() => {
    if (!forumId) return;
    dispatch(fetchSingleForum(forumId));
    dispatch(
      fetchComments({ forumId, page: commentPage, size: COMMENTS_PER_PAGE }),
    );
    if (user) dispatch(checkLike({ forumId }));
  }, [dispatch, forumId]);

  // Refetch comments when page changes
  useEffect(() => {
    if (!forumId) return;
    dispatch(
      fetchComments({ forumId, page: commentPage, size: COMMENTS_PER_PAGE }),
    );
  }, [dispatch, forumId, commentPage]);

  useEffect(() => {
    const loadForum = async () => {
      if (!forumId) return;
      try {
        // fetch forum data
        await dispatch(fetchSingleForum(forumId)).unwrap();

        // check if current user liked it (Redux will store isLiked)
        if (user) {
          await dispatch(checkLike({ forumId })).unwrap();
        }
      } catch (err) {
        showToast("Error loading forum", "destructive");
      }
    };

    loadForum();
  }, [forumId]);

  // Show toast for errors
  useEffect(() => {
    if (error) showToast(error, "destructive");
  }, [error]);

  const handleAddComment = async () => {
    if (!forumId || newComment.trim() === "") return;
    setAddingComment(true);
    try {
      await dispatch(createComment({ forumId, text: newComment })).unwrap();
      dispatch(fetchComments({ forumId, page: 0, size: COMMENTS_PER_PAGE }));
      // if on first page, refetch first page comments
      if (commentPage === 0) {
        await dispatch(
          fetchComments({ forumId, page: 0, size: COMMENTS_PER_PAGE }),
        ).unwrap();
      }

      dispatch(fetchSingleForum(forumId)); // refresh counts
      setNewComment("");
      showToast("Comment added successfully!", "default");
    } catch {
      showToast("Error adding comment. Please try again.", "destructive");
    } finally {
      setAddingComment(false);
    }
  };

  const handleEditComment = (comment: IComment) => {
    setEditingCommentId(comment.commentId);
    setEditText(comment.text);
  };

  const handleSaveEdit = async (commentId: string) => {
    try {
      await dispatch(updateComment({ commentId, text: editText })).unwrap();
      setEditingCommentId(null);
      showToast("Comment updated successfully!", "default");
    } catch {
      showToast("Error updating comment.", "destructive");
    }
  };

  const handleDeleteComment = async (commentId: string) => {
    try {
      await dispatch(deleteComment({ commentId })).unwrap();

      // compute new total pages after deletion
      const updatedCount = Math.max((forum?.commentsCount || 1) - 1, 0);
      const newTotalPages = Math.max(
        Math.ceil(updatedCount / COMMENTS_PER_PAGE),
        1,
      );

      // if current page is out of bounds, move to last valid page
      let newPage = commentPage;
      if (commentPage > newTotalPages - 1) {
        newPage = newTotalPages - 1;
        setCommentPage(newPage); // update local state or dispatch setCommentPage
      }

      // fetch updated comments for the correct page
      if (forumId) {
        dispatch(
          fetchComments({ forumId, page: newPage, size: COMMENTS_PER_PAGE }),
        );
        dispatch(fetchSingleForum(forumId)); // update forum count
      }

      showToast("Comment deleted successfully", "default");
    } catch {
      showToast("Failed to delete comment", "destructive");
    }
  };

  const handleLike = async () => {
    if (!forumId || likeProcessing) return;

    if (!user) {
      showToast("Please log in to like this post.", "destructive");
      return;
    }

    try {
      await dispatch(toggleLike({ forumId })).unwrap();

      // refresh forum counts after like/unlike
      await dispatch(fetchSingleForum(forumId));

      // optional: re-check like status from backend
      if (user) await dispatch(checkLike({ forumId }));
    } catch (err) {
      console.error(err);
      showToast("Failed to like the forum. Please try again.", "destructive");
    }
  };

  if (loading && !forum) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <LoadingSpinner />
      </div>
    );
  }

  if (error && !forum) {
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
            </AvatarFallback>
          </Avatar>
          <div>
            <p className="text-sm font-semibold">{`${forum?.firstName || ""} ${forum?.lastName || ""}`}</p>
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
            {isLiked ? (
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
            {forum?.commentsCount || 0} <FaRegMessage className="h-4 w-4" />
          </Button>
        </div>

        {forum?.tags?.length ? (
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
        ) : null}

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
                    <p className="text-sm font-semibold">{`${comment?.firstName || ""} ${comment?.lastName || ""}`}</p>

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

                {comment.userId === Number(currentUserId) &&
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
              onPageChange={(page) => dispatch(setCommentPage(page))}
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
            />
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
