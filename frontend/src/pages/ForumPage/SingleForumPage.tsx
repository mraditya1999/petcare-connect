import { useEffect, useState, useCallback } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import { LoadingSpinner } from "@/components/ui/LoadingSpinner";
import { useAppDispatch, useAppSelector } from "@/app/hooks";
import { getUserFromStorage, formatRelativeTime } from "@/utils/helpers";
import { ROUTES } from "@/utils/constants";
import {
  FaHeart,
  FaRegHeart,
  FaRegMessage,
  FaTrash,
  FaPenToSquare,
} from "react-icons/fa6";
import {
  fetchSingleForum,
  fetchComments,
  createComment,
  updateComment,
  deleteComment,
  toggleLike,
  checkLike,
  deleteForum,
  updateForum,
} from "@/features/forum/forumDetailThunk";
import {
  setCommentPage,
  selectIsLiked,
} from "@/features/forum/forumDetailSlice";
import {
  ShowToast,
  ConfirmDialog,
  ForumEditor,
  PaginationControl,
} from "@/components";
import {
  createCommentSchema,
  updateCommentSchema,
  updateForumSchema,
} from "@/utils/validations";
import { Input } from "@/components/ui/input";

const COMMENTS_PER_PAGE = 5;

const SingleForumPage = () => {
  const { forumId } = useParams<{ forumId: string }>();
  const navigate = useNavigate();
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

  // local state
  const [newComment, setNewComment] = useState("");
  const [addingComment, setAddingComment] = useState(false);
  const [editingCommentId, setEditingCommentId] = useState<string | null>(null);
  const [editText, setEditText] = useState("");
  const [showCommentBox, setShowCommentBox] = useState(false);

  // State for Comment Deletion Dialog
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [toDeleteCommentId, setToDeleteCommentId] = useState<string | null>(
    null,
  );

  const [editingForum, setEditingForum] = useState(false);
  const [editForumText, setEditForumText] = useState(forum?.content || "");
  const [editForumTitle, setEditForumTitle] = useState(forum?.title || "");
  const [deleteForumDialogOpen, setDeleteForumDialogOpen] = useState(false);
  const [updateErrors, setUpdateErrors] = useState<{
    title?: string;
    content?: string;
    tags?: string;
  }>({});

  // Tag States
  const [editForumTags, setEditForumTags] = useState<string[]>(
    forum?.tags || [],
  );
  const [editForumTagsText, setEditForumTagsText] = useState(
    forum?.tags?.join(",") || "",
  );

  const [addCommentError, setAddCommentError] = useState<string | null>(null);
  const [updateCommentError, setUpdateCommentError] = useState<string | null>(
    null,
  );

  useEffect(() => {
    if (forum) {
      setEditForumTitle(forum.title);
      setEditForumText(forum.content);
      // Initialize both states from the Redux state
      const tagsArray = forum.tags || [];
      setEditForumTags(tagsArray);
      setEditForumTagsText(tagsArray.join(","));
    }
  }, [forum]);

  // Fetch forum + initial comments
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

  // CRITICAL FIX: Use useEffect for robust state cleanup after dialog close
  useEffect(() => {
    if (!deleteDialogOpen) {
      // This runs reliably after the dialog has been closed and fully unmounted/released focus.
      setToDeleteCommentId(null);
    }
  }, [deleteDialogOpen]);

  useEffect(() => {
    if (error) ShowToast({ description: error, type: "error" });
  }, [error]);

  const handleUpdateForum = async (
    forumId: string,
    title: string,
    content: string,
    tags: string[] = [],
  ) => {
    // This cleanup logic is important and uses the array passed from the button click/saved state
    const cleanTags = tags
      .map((tag) => tag.trim())
      .filter((tag) => tag.length > 0);

    const result = updateForumSchema.safeParse({
      title,
      content,
      tags: cleanTags,
    });

    if (!result.success) {
      const fieldErrors = result.error.flatten().fieldErrors;
      setUpdateErrors({
        title: fieldErrors.title?.[0],
        content: fieldErrors.content?.[0],
        tags: fieldErrors.tags?.[0],
      });
      return;
    }

    try {
      await dispatch(updateForum({ forumId, ...result.data })).unwrap();
      setEditingForum(false);
      setUpdateErrors({});
      ShowToast({
        description: "Forum updated successfully!",
        type: "success",
      });
    } catch (err) {
      ShowToast({ description: "Failed to update forum", type: "error" });
      console.error(err);
    }
  };

  const handleDeleteForum = (forumId: string) => {
    dispatch(deleteForum({ forumId }))
      .unwrap()
      .then(() => {
        ShowToast({
          description: "Forum deleted successfully!",
          type: "success",
        });
        navigate(`${ROUTES.FORUM}`);
      })
      .catch((err) => {
        ShowToast({ description: "Failed to delete forum", type: "error" });
        console.error(err);
      });
  };

  const handleAddComment = useCallback(async () => {
    // Run Zod validation first
    const result = createCommentSchema.safeParse({
      forumId,
      userId: currentUserId,
      text: newComment,
      parentId: undefined,
    });

    if (!result.success) {
      const errors = result.error.flatten().fieldErrors;
      setAddCommentError(errors.text?.[0] || "Invalid comment");
      return;
    }

    try {
      await dispatch(createComment(result.data)).unwrap();
      dispatch(
        fetchComments({
          forumId: result.data.forumId,
          page: 0,
          size: COMMENTS_PER_PAGE,
        }),
      );
      dispatch(fetchSingleForum(result.data.forumId));
      setNewComment("");
      setAddCommentError(null);
      ShowToast({
        description: "Comment added successfully!",
        type: "success",
      });
    } catch {
      ShowToast({
        description: "Error adding comment. Please try again.",
        type: "error",
      });
    } finally {
      setAddingComment(false);
    }
  }, [dispatch, forumId, newComment, currentUserId]);

  const handleSaveEdit = useCallback(
    async (commentId: string) => {
      // Validate text before sending
      const result = updateCommentSchema.safeParse({
        commentId,
        text: editText,
      });

      if (!result.success) {
        const errors = result.error.flatten().fieldErrors;
        setUpdateCommentError(errors.text?.[0] || "Invalid comment");
        return;
      }
      try {
        await dispatch(
          updateComment({ commentId, text: result.data.text }),
        ).unwrap();
        setEditingCommentId(null);
        setUpdateCommentError(null);
        ShowToast({
          description: "Comment updated successfully!",
          type: "success",
        });
      } catch (err) {
        console.error(err);
        ShowToast({ description: "Error updating comment.", type: "error" });
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
        ShowToast({
          description: "Comment deleted successfully",
          type: "success",
        });
      } catch {
        ShowToast({ description: "Failed to delete comment", type: "error" });
      }
    },
    [dispatch, forumId, forum?.commentsCount, commentPage],
  );

  const handleLike = useCallback(async () => {
    if (!forumId || likeProcessing) return;
    if (!user) {
      ShowToast({
        description: "Please log in to like this post.",
        type: "error",
      });

      return;
    }
    try {
      await dispatch(toggleLike({ forumId })).unwrap();
      dispatch(fetchSingleForum(forumId));
      dispatch(checkLike({ forumId }));
    } catch {
      ShowToast({
        description: "Failed to like the forum. Please try again.",
        type: "error",
      });
    }
  }, [dispatch, forumId, likeProcessing, user]);

  // Loading / error fallback
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
      <div className="section-width mx-auto mt-6 space-y-6 rounded-lg border p-8 shadow-lg">
        <div className="flex items-center justify-between rounded-t-lg bg-card p-6">
          <div className="flex items-center space-x-3">
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

          {forum?.userId === Number(currentUserId) && (
            <div className="flex items-center">
              {/* Edit button */}
              <Button
                variant="ghost"
                onClick={() => {
                  setEditingForum(true);
                  setEditForumTitle(forum?.title || "");
                  setEditForumText(forum?.content || "");
                  // Ensure both text and array state are correctly initialized on edit click
                  const tagsArray = forum?.tags || [];
                  setEditForumTags(tagsArray);
                  setEditForumTagsText(tagsArray.join(","));
                }}
                title="Edit forum"
              >
                <FaPenToSquare className="text-muted-foreground hover:text-foreground" />
              </Button>

              {/* Delete button */}
              <Button
                variant="ghost"
                onClick={() => setDeleteForumDialogOpen(true)}
                title="Delete forum"
              >
                <FaTrash className="text-destructive hover:text-destructive/80" />
              </Button>
            </div>
          )}
        </div>

        {/* Forum Content */}
        {editingForum ? (
          <div className="space-y-4">
            <Input
              value={editForumTitle}
              onChange={(e) => {
                setEditForumTitle(e.target.value);
                if (updateErrors.title)
                  setUpdateErrors((prev) => ({ ...prev, title: undefined }));
              }}
              placeholder="Forum title"
            />
            {updateErrors.title && (
              <p className="text-sm text-red-500">{updateErrors.title}</p>
            )}

            {/* TAGS INPUT FIXED */}
            <Input
              placeholder="Tags (comma-separated)"
              value={editForumTagsText}
              onChange={(e) => {
                const newText = e.target.value;
                setEditForumTagsText(newText);

                // CRITICAL FIX: Update the array state (editForumTags) whenever the text changes
                const newTagsArray = newText
                  .split(",")
                  .map((tag) => tag.trim())
                  .filter((tag) => tag.length > 0);

                setEditForumTags(newTagsArray);

                if (updateErrors.tags) {
                  setUpdateErrors((prev) => ({ ...prev, tags: undefined }));
                }
              }}
            />

            {updateErrors.tags && (
              <p className="text-sm text-red-500">{updateErrors.tags}</p>
            )}

            <ForumEditor
              value={editForumText}
              onChange={(val) => {
                setEditForumText(val);
                if (updateErrors.content)
                  setUpdateErrors((prev) => ({ ...prev, content: undefined }));
              }}
              placeholder="Update your forum content..."
            />
            {updateErrors.content && (
              <p className="text-sm text-red-500">{updateErrors.content}</p>
            )}

            <div className="flex gap-2">
              <Button
                onClick={() =>
                  handleUpdateForum(
                    forum!.forumId,
                    editForumTitle,
                    editForumText,
                    // Pass the synchronized array state
                    editForumTags,
                  )
                }
              >
                Save
              </Button>

              <Button variant="outline" onClick={() => setEditingForum(false)}>
                Cancel
              </Button>
            </div>
          </div>
        ) : (
          <>
            <h1 className="word-break-all break-words text-3xl font-bold text-foreground">
              {forum?.title}
            </h1>
            <div
              className="prose prose-sm max-w-full overflow-x-auto break-words text-foreground"
              dangerouslySetInnerHTML={{ __html: forum?.content || "" }}
            />
          </>
        )}

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

        <ConfirmDialog
          open={deleteForumDialogOpen}
          onClose={setDeleteForumDialogOpen}
          onConfirm={() => handleDeleteForum(forum!.forumId)}
          title="Delete forum?"
          description="Are you sure you want to delete this forum? This action cannot be undone."
          confirmText="Delete"
          cancelText="Cancel"
          confirmVariant="destructive"
        />

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
                          onChange={(e) => {
                            setEditText(e.target.value);
                            if (updateCommentError) setUpdateCommentError(null);
                          }}
                          placeholder="Edit your comment..."
                        />
                        {updateCommentError && (
                          <p className="text-sm text-red-500">
                            {updateCommentError}
                          </p>
                        )}
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
                    <div className="flex w-fit space-x-1">
                      {/* 💡 DEBUGGING FIX: Direct Edit Button */}
                      <Button
                        variant="ghost"
                        size="icon"
                        className="h-8 w-8 text-muted-foreground hover:bg-muted"
                        onClick={() => {
                          setEditingCommentId(comment.commentId);
                          setEditText(comment.text);
                        }}
                        title="Edit comment"
                      >
                        <FaPenToSquare className="h-4 w-4" />
                      </Button>

                      {/* 💡 DEBUGGING FIX: Direct Delete Button */}
                      <Button
                        variant="ghost"
                        size="icon"
                        className="h-8 w-8 text-destructive hover:bg-destructive/10"
                        onClick={() => {
                          setToDeleteCommentId(String(comment.commentId));
                          setDeleteDialogOpen(true);
                        }}
                        title="Delete comment"
                      >
                        <FaTrash className="h-4 w-4" />
                      </Button>
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

        {/* Comment Deletion Confirm Dialog */}
        <ConfirmDialog
          open={deleteDialogOpen}
          // Only manage the dialog's open state here
          onClose={setDeleteDialogOpen}
          onConfirm={async () => {
            if (toDeleteCommentId !== null) {
              await handleDeleteComment(toDeleteCommentId);
            }
          }}
          title="Delete comment?"
          description="Are you sure you want to delete this comment? This action cannot be undone."
          confirmText="Delete"
          cancelText="Cancel"
          confirmVariant="destructive"
        />

        {/* Add Comment */}
        {user && (
          <div className="mt-4 rounded-lg bg-card p-6 shadow-md">
            <h2 className="mb-2 text-lg font-semibold text-foreground">
              Add a Comment
            </h2>
            <textarea
              className="mb-2 w-full rounded-md border border-border bg-background p-2 text-sm text-foreground placeholder:text-muted-foreground"
              rows={3}
              placeholder="Add a comment..."
              value={newComment}
              onChange={(e) => {
                setNewComment(e.target.value);
                if (addCommentError) setAddCommentError(null);
              }}
            />
            {addCommentError && (
              <p className="text-sm text-red-500">{addCommentError}</p>
            )}

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
