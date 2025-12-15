import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { IForumDetailState } from "@/types/forum-types";
import { RootState } from "@/app/store";
import {
  fetchSingleForum,
  fetchComments,
  createComment,
  updateComment,
  deleteComment,
  toggleLike,
  checkLike,
  updateForum,
  deleteForum,
} from "./forumDetailThunk";
import {
  IFetchSingleForumResponse,
  IFetchCommentsResponse,
  ICreateCommentResponse,
  IUpdateCommentResponse,
  IDeleteCommentResponse,
  IToggleLikeResponse,
  ICheckLikeResponse,
  IUpdateForumResponse,
} from "@/types/forum-thunk-types";

const initialState: IForumDetailState = {
  forum: null,
  comments: [],
  commentPage: 0,
  totalCommentPages: 1,
  totalCommentElements: 0,
  loading: false,
  error: null,
  likeProcessing: false,
  isLiked: false,
};

const COMMENTS_PER_PAGE = 5;
const forumDetailSlice = createSlice({
  name: "forumDetail",
  initialState,
  reducers: {
    setCommentPage(state, action: PayloadAction<number>) {
      state.commentPage = action.payload;
    },
    clearForumDetail(state) {
      Object.assign(state, initialState);
    },
  },

  extraReducers: (builder) => {
    builder
      .addCase(fetchSingleForum.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(
        fetchSingleForum.fulfilled,
        (state, action: PayloadAction<IFetchSingleForumResponse>) => {
          state.loading = false;
          state.forum = action.payload.data;
        },
      )
      .addCase(fetchSingleForum.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Failed to fetch forum";
      });
    builder
      .addCase(fetchComments.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(
        fetchComments.fulfilled,
        (state, action: PayloadAction<IFetchCommentsResponse>) => {
          state.loading = false;
          state.comments = action.payload.data.content ?? [];

          const totalElements =
            (action.payload.data as unknown as {
              totalElements?: number;
              page?: { totalElements?: number };
            }).totalElements ??
            (action.payload.data as unknown as { page?: { totalElements?: number } }).page
              ?.totalElements ??
            0;

          const totalPages =
            (action.payload.data as unknown as {
              totalPages?: number;
              page?: { totalPages?: number };
            }).totalPages ??
            (action.payload.data as unknown as { page?: { totalPages?: number } }).page
              ?.totalPages ??
            1;

          state.totalCommentElements = totalElements;
          state.totalCommentPages = Math.max(totalPages, 1);
        },
      )
      .addCase(fetchComments.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Failed to fetch comments";
      });
    builder
      .addCase(createComment.pending, (state) => {
        state.loading = true;
      })
      .addCase(
        createComment.fulfilled,
        (state, action: PayloadAction<ICreateCommentResponse>) => {
          state.loading = false;
          if (state.forum) state.forum.commentsCount++;

          if (action.payload.data) {
            state.comments = [action.payload.data, ...state.comments].slice(
              0,
              COMMENTS_PER_PAGE,
            );
          }
        },
      )
      .addCase(createComment.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Failed to add comment";
      });
    builder.addCase(
      updateComment.fulfilled,
      (state, action: PayloadAction<IUpdateCommentResponse>) => {
        const updated = action.payload.data;
        state.comments = state.comments.map((c) =>
          c.commentId === updated.commentId ? { ...c, ...updated } : c,
        );
      },
    );
    builder.addCase(
      deleteComment.fulfilled,
      (state, action: PayloadAction<IDeleteCommentResponse>) => {
        const deletedId = action.payload.commentId;
        state.comments = state.comments.filter(
          (c) => c.commentId !== deletedId,
        );

        if (state.forum) {
          state.forum.commentsCount = Math.max(
            state.forum.commentsCount - 1,
            0,
          );
        }
      },
    );
    builder
      .addCase(toggleLike.pending, (state) => {
        state.likeProcessing = true;
      })
      .addCase(
        toggleLike.fulfilled,
        (state, action: PayloadAction<IToggleLikeResponse>) => {
          state.likeProcessing = false;
          if (!state.forum) return;

          const msg = action.payload.message.toLowerCase();

          if (msg.includes("unlike")) {
            state.isLiked = false;
            state.forum.likesCount = Math.max(state.forum.likesCount - 1, 0);
          } else if (msg.includes("like")) {
            state.isLiked = true;
            state.forum.likesCount++;
          }
        },
      )
      .addCase(toggleLike.rejected, (state, action) => {
        state.likeProcessing = false;
        state.error = action.payload || "Failed to toggle like";
      });
    builder.addCase(
      checkLike.fulfilled,
      (state, action: PayloadAction<ICheckLikeResponse>) => {
        state.isLiked = !!action.payload.data?.isLiked;
      },
    );
    builder
      .addCase(updateForum.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(
        updateForum.fulfilled,
        (state, action: PayloadAction<IUpdateForumResponse>) => {
          state.loading = false;
          if (state.forum && action.payload.data) {
            state.forum = { ...state.forum, ...action.payload.data };
          }
        },
      )
      .addCase(updateForum.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Failed to update forum";
      });

    builder
      .addCase(deleteForum.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(deleteForum.fulfilled, (state) => {
        state.loading = false;
        state.forum = null;
        state.comments = [];
        state.totalCommentPages = 1;
        state.totalCommentElements = 0;
      })
      .addCase(deleteForum.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Failed to delete forum";
      });
  },
});

export const { setCommentPage, clearForumDetail } = forumDetailSlice.actions;

export const selectForumDetail = (state: RootState) => state.forumDetail;
export const selectForum = (state: RootState) => state.forumDetail.forum;
export const selectComments = (state: RootState) => state.forumDetail.comments;
export const selectIsLiked = (state: RootState) => state.forumDetail.isLiked;
export default forumDetailSlice.reducer;
