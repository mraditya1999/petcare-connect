import { useEffect, useState, useCallback } from "react";
import { useParams } from "react-router-dom";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import { LoadingSpinner } from "@/components/ui/LoadingSpinner";
import { useAppDispatch, useAppSelector } from "@/app/hooks";
import { PaginationControl } from "@/components";
import {
  DropdownMenu,
  DropdownMenuTrigger,
  DropdownMenuContent,
  DropdownMenuItem,
} from "@/components/ui/dropdown-menu";

import {
  AlertDialog,
  AlertDialogContent,
  AlertDialogHeader,
  AlertDialogFooter,
  AlertDialogTitle,
  AlertDialogDescription,
  AlertDialogCancel,
  AlertDialogAction,
} from "@/components/ui/alert-dialog";
import {
  FaHeart,
  FaRegHeart,
  FaRegMessage,
  FaEllipsisVertical,
} from "react-icons/fa6";
import {
  fetchSingleForum,
  fetchComments,
  createComment,
  updateComment,
  deleteComment,
  toggleLike,
  checkLike,
} from "@/features/forum/forumDetailThunk";
import {
  getUserFromStorage,
  showToast,
  formatRelativeTime,
} from "@/utils/helpers";
import {
  setCommentPage,
  selectIsLiked,
} from "@/features/forum/forumDetailSlice";

const COMMENTS_PER_PAGE = 5;

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

  const user = getUserFromStorage();
  const currentUserId = user?.data?.userId;
  const isLiked = useAppSelector(selectIsLiked);

  const [newComment, setNewComment] = useState("");
  const [addingComment, setAddingComment] = useState(false);
  const [editingCommentId, setEditingCommentId] = useState<string | null>(null);
  const [editText, setEditText] = useState("");
  const [showCommentBox, setShowCommentBox] = useState(false);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [toDeleteCommentId, setToDeleteCommentId] = useState<string | null>(
    null,
  );

  // Fetch forum and initial comments
  useEffect(() => {
    if (!forumId) return;

    dispatch(fetchSingleForum(forumId));
    dispatch(
      fetchComments({ forumId, page: commentPage, size: COMMENTS_PER_PAGE }),
    );

    if (user) dispatch(checkLike({ forumId }));
  }, [dispatch, forumId]);

  // Refetch comments on page change
  useEffect(() => {
    if (!forumId) return;
    dispatch(
      fetchComments({ forumId, page: commentPage, size: COMMENTS_PER_PAGE }),
    );
  }, [dispatch, forumId, commentPage]);

  useEffect(() => {
    if (error) showToast(error, "destructive");
  }, [error]);

  const handleAddComment = useCallback(async () => {
    if (!forumId || !newComment.trim()) return;

    setAddingComment(true);
    try {
      await dispatch(createComment({ forumId, text: newComment })).unwrap();
      dispatch(fetchComments({ forumId, page: 0, size: COMMENTS_PER_PAGE }));
      dispatch(fetchSingleForum(forumId));
      setNewComment("");
      showToast("Comment added successfully!", "default");
    } catch {
      showToast("Error adding comment. Please try again.", "destructive");
    } finally {
      setAddingComment(false);
    }
  }, [dispatch, forumId, newComment]);

  const handleSaveEdit = useCallback(
    async (commentId: string) => {
      try {
        await dispatch(updateComment({ commentId, text: editText })).unwrap();
        setEditingCommentId(null);
        showToast("Comment updated successfully!", "default");
      } catch {
        showToast("Error updating comment.", "destructive");
      }
    },
    [dispatch, editText],
  );

  const handleDeleteComment = useCallback(
    async (commentId: string) => {
      if (!forumId) return;

      try {
        await dispatch(deleteComment({ commentId })).unwrap();

        const updatedCount = Math.max((forum?.commentsCount || 1) - 1, 0);
        const newTotalPages = Math.max(
          Math.ceil(updatedCount / COMMENTS_PER_PAGE),
          1,
        );
        const newPage = Math.min(commentPage, newTotalPages - 1);

        if (newPage !== commentPage) dispatch(setCommentPage(newPage));

        dispatch(
          fetchComments({ forumId, page: newPage, size: COMMENTS_PER_PAGE }),
        );
        dispatch(fetchSingleForum(forumId));

        showToast("Comment deleted successfully", "default");
      } catch {
        showToast("Failed to delete comment", "destructive");
      }
    },
    [dispatch, forumId, forum?.commentsCount, commentPage],
  );

  const handleLike = useCallback(async () => {
    if (!forumId || likeProcessing) return;

    if (!user) {
      showToast("Please log in to like this post.", "destructive");
      return;
    }

    try {
      await dispatch(toggleLike({ forumId })).unwrap();
      dispatch(fetchSingleForum(forumId));
      dispatch(checkLike({ forumId }));
    } catch {
      showToast("Failed to like the forum. Please try again.", "destructive");
    }
  }, [dispatch, forumId, likeProcessing, user]);

  if (loading && !forum)
    return (
      <div className="flex min-h-screen items-center justify-center">
        <LoadingSpinner />
      </div>
    );

  if (error && !forum)
    return (
      <div className="flex min-h-screen items-center justify-center">
        <p className="text-red-600">Something went wrong. Please try again.</p>
      </div>
    );

  return (
    <section className="py-16">
      <div className="section-width mx-auto mt-6 space-y-6 rounded-lg border p-8 shadow-lg">
        {/* Forum Header */}
        <div className="flex items-center space-x-3 rounded-t-lg bg-card p-6">
          <Avatar className="h-10 w-10">
            <AvatarImage src={forum?.firstName || ""} alt="User Avatar" />
            <AvatarFallback>
              {forum?.firstName?.slice(0, 1) || "?"}
            </AvatarFallback>
          </Avatar>
          <div>
            <p className="text-sm font-semibold text-foreground">
              {`${forum?.firstName || ""} ${forum?.lastName || ""}`}
            </p>
            <p className="text-xs text-muted-foreground">
              {forum?.createdAt
                ? formatRelativeTime(forum.createdAt)
                : "Just now"}
            </p>
          </div>
        </div>

        {/* Forum Content */}
        <h1 className="text-3xl font-bold text-foreground">{forum?.title}</h1>
        <div
          className="prose prose-sm max-w-none text-foreground"
          dangerouslySetInnerHTML={{ __html: forum?.content || "" }}
        />

        {/* Actions */}
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
              <FaRegHeart className="h-4 w-4 text-muted-foreground transition-colors duration-200" />
            )}
            <span className="ml-1 text-muted-foreground">
              {forum?.likesCount || 0}
            </span>
          </Button>

          <Button
            variant="ghost"
            className="flex items-center gap-1 p-0 hover:bg-transparent"
            onClick={() => setShowCommentBox(!showCommentBox)}
            disabled={!user || likeProcessing}
            title={!user ? "Log in to comment this forum" : undefined}
          >
            <span className="text-muted-foreground">
              {forum?.commentsCount || 0}
            </span>
            <FaRegMessage className="h-4 w-4 text-muted-foreground" />
          </Button>
        </div>

        {/* Tags */}
        {forum?.tags?.length ? (
          <div className="mt-4 flex flex-wrap gap-2">
            {forum.tags.map((tag) => (
              <span
                key={tag}
                className="rounded-md bg-muted px-2 py-1 text-xs text-muted-foreground"
              >
                #{tag}
              </span>
            ))}
          </div>
        ) : null}

        {/* Comments */}
        <div className="mt-4 rounded-lg bg-card p-6 shadow-md">
          <h2 className="text-lg font-semibold text-foreground">Comments</h2>
          <></>
          {comments.length ? (
            comments.map((comment) => (
              <div
                key={comment.commentId}
                className="mb-3 flex justify-between rounded-xl border border-border bg-card p-4 shadow"
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
                    <p className="text-sm font-semibold text-foreground">
                      {`${comment?.firstName || ""} ${comment?.lastName || ""}`}
                    </p>

                    {editingCommentId === comment.commentId ? (
                      <div className="mt-2 w-full">
                        <textarea
                          className="w-full rounded-md border border-border bg-background p-2 text-sm text-foreground placeholder:text-muted-foreground focus:outline-none focus:ring focus:ring-primary/20"
                          rows={3}
                          value={editText}
                          onChange={(e) => setEditText(e.target.value)}
                          placeholder="Edit your comment..."
                        />
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
                        className="prose mt-1 max-w-none text-sm text-foreground"
                        dangerouslySetInnerHTML={{ __html: comment.text }}
                      />
                    )}

                    <p className="mt-1 text-xs text-muted-foreground">
                      {comment?.createdAt
                        ? formatRelativeTime(comment.createdAt)
                        : "Just now"}
                    </p>
                  </div>
                </div>

                {comment.userId === Number(currentUserId) &&
                  editingCommentId !== comment.commentId && (
                    <div className="flex w-6 justify-end">
                      <DropdownMenu>
                        <DropdownMenuTrigger asChild>
                          <button
                            type="button"
                            className="text-muted-foreground hover:text-foreground"
                          >
                            <FaEllipsisVertical size={18} />
                          </button>
                        </DropdownMenuTrigger>

                        <DropdownMenuContent align="end" className="w-32">
                          <DropdownMenuItem
                            onClick={() => {
                              setEditingCommentId(comment.commentId);
                              setEditText(comment.text);
                            }}
                          >
                            Edit
                          </DropdownMenuItem>

                          <DropdownMenuItem
                            className="text-destructive"
                            onClick={() => {
                              setToDeleteCommentId(String(comment.commentId));
                              setTimeout(() => setDeleteDialogOpen(true), 0);
                            }}
                          >
                            Delete
                          </DropdownMenuItem>
                        </DropdownMenuContent>
                      </DropdownMenu>
                    </div>
                  )}
              </div>
            ))
          ) : (
            <p className="text-muted-foreground">No comments yet.</p>
          )}

          {totalCommentPages > 1 && (
            <PaginationControl
              currentPage={commentPage}
              totalPages={totalCommentPages}
              onPageChange={(page) => dispatch(setCommentPage(page))}
            />
          )}
        </div>

        <AlertDialog
          open={deleteDialogOpen}
          onOpenChange={(open) => {
            setDeleteDialogOpen(open);
            if (!open) {
              setToDeleteCommentId(null);
            }
          }}
        >
          <AlertDialogContent>
            <AlertDialogHeader>
              <AlertDialogTitle className="text-foreground">
                Delete comment?
              </AlertDialogTitle>
              <AlertDialogDescription className="text-muted-foreground">
                Are you sure you want to delete this comment? This action cannot
                be undone.
              </AlertDialogDescription>
            </AlertDialogHeader>

            <AlertDialogFooter>
              <AlertDialogCancel>Cancel</AlertDialogCancel>

              <AlertDialogAction
                className="bg-destructive text-destructive-foreground hover:bg-destructive/90"
                onClick={() => {
                  if (toDeleteCommentId !== null) {
                    handleDeleteComment(toDeleteCommentId);
                  }
                  setDeleteDialogOpen(false);
                  setToDeleteCommentId(null);
                }}
              >
                Delete
              </AlertDialogAction>
            </AlertDialogFooter>
          </AlertDialogContent>
        </AlertDialog>

        {/* Add Comment */}
        {user && (
          <div className="mt-4 rounded-lg bg-card p-6 shadow-md">
            <h2 className="mb-2 text-lg font-semibold text-foreground">
              Add a Comment
            </h2>
            <textarea
              className="mb-4 w-full rounded-md border border-border bg-background p-2 text-sm text-foreground placeholder:text-muted-foreground"
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
