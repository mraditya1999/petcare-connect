// src/features/forum/forumDetailSlice.ts
import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import {
  fetchSingleForum,
  fetchComments,
  createComment,
  updateComment,
  deleteComment,
  toggleLike,
  checkLike,
} from "./forumDetailThunk";
import {
  IForum,
  IComment,
  ICommentListResponse,
  ISingleForumResponse,
  IToggleLikeResponse,
  ICheckLikeResponse,
} from "@/types/forum-types";
import { RootState } from "@/app/store";

export interface ForumDetailState {
  forum: IForum | null;
  comments: IComment[];
  commentPage: number;
  totalCommentPages: number;
  loading: boolean;
  error: string | null;
  likeProcessing: boolean;
  isLiked: boolean;
}

const initialState: ForumDetailState = {
  forum: null,
  comments: [],
  commentPage: 0,
  totalCommentPages: 1,
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
    // =======================
    // FETCH SINGLE FORUM
    // =======================
    builder
      .addCase(fetchSingleForum.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(
        fetchSingleForum.fulfilled,
        (state, action: PayloadAction<ISingleForumResponse>) => {
          state.loading = false;
          state.forum = action.payload.data;
        },
      )
      .addCase(fetchSingleForum.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Failed to fetch forum";
      });

    // =======================
    // FETCH COMMENTS
    // =======================
    builder
      .addCase(fetchComments.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(
        fetchComments.fulfilled,
        (state, action: PayloadAction<ICommentListResponse>) => {
          state.loading = false;
          state.comments = action.payload.data.content ?? [];
          const rawPages = action.payload.data.page?.totalPages ?? 1;
          state.totalCommentPages = Math.max(rawPages, 1); // FIXED
        },
      )
      .addCase(fetchComments.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Failed to fetch comments";
      });

    // =======================
    // CREATE COMMENT
    // =======================
    builder
      .addCase(createComment.pending, (state) => {
        state.loading = true;
      })
      .addCase(createComment.fulfilled, (state, action) => {
        state.loading = false;
        if (state.forum) state.forum.commentsCount++;

        if (action.payload.data) {
          // prepend new comment to the comments list
          state.comments = [action.payload.data, ...state.comments].slice(
            0,
            COMMENTS_PER_PAGE,
          );
        }
      })

      .addCase(createComment.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Failed to add comment";
      });

    // =======================
    // UPDATE COMMENT
    // =======================
    builder.addCase(updateComment.fulfilled, (state, action) => {
      const updated = action.payload.data;
      state.comments = state.comments.map((c) =>
        c.commentId === updated.commentId ? { ...c, ...updated } : c,
      );
    });

    // =======================
    // DELETE COMMENT
    // =======================
    builder.addCase(deleteComment.fulfilled, (state, action) => {
      const deletedId = action.payload.commentId;
      state.comments = state.comments.filter((c) => c.commentId !== deletedId);

      if (state.forum) {
        state.forum.commentsCount = Math.max(state.forum.commentsCount - 1, 0);
      }
    });

    // =======================
    // TOGGLE LIKE (LIKE / UNLIKE)
    // =======================
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

    // =======================
    // CHECK LIKE
    // =======================
    builder.addCase(
      checkLike.fulfilled,
      (state, action: PayloadAction<ICheckLikeResponse>) => {
        state.isLiked = !!action.payload.data?.isLiked;
      },
    );
  },
});

export const { setCommentPage, clearForumDetail } = forumDetailSlice.actions;

export const selectForumDetail = (state: RootState) => state.forumDetail;
export const selectForum = (state: RootState) => state.forumDetail.forum;
export const selectComments = (state: RootState) => state.forumDetail.comments;
export const selectIsLiked = (state: RootState) => state.forumDetail.isLiked;
export default forumDetailSlice.reducer;
