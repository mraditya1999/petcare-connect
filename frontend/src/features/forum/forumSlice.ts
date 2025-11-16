// src/features/forum/forumSlice.ts
import { createSlice } from "@reduxjs/toolkit";
import {
  addComment,
  updateComment,
  deleteComment,
  fetchComments,
} from "./forumThunk";

import { IForumState } from "@/types/forum-types";
import { showToast } from "@/utils/helpers";

const initialState: IForumState = {
  forum: null,
  comments: [],
  commentPage: 0,
  totalCommentPages: 1,
  loading: false,
  error: null,
};

export const forumSlice = createSlice({
  name: "forum",
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      /* -------------------------
       * FETCH SINGLE FORUM
       * ------------------------- */
      .addCase(fetchComments.pending, (state) => {
        state.loading = true;
      })
      .addCase(fetchComments.fulfilled, (state, action) => {
        state.loading = false;
        state.comments = action.payload.content;
        state.commentPage = action.payload.page.number;
        state.totalCommentPages = action.payload.page.totalPages;
      })

      .addCase(fetchComments.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      })

      /* -------------------------
       * ADD COMMENT
       * ------------------------- */
      .addCase(addComment.pending, (state) => {
        state.error = null;
      })
      .addCase(addComment.fulfilled, (state, action) => {
        if (state.commentPage === 0) {
          state.comments.unshift(action.payload);
          state.comments = state.comments.slice(0, 5);
        }
        state.forum!.commentsCount++;
        state.totalCommentPages = Math.ceil(state.forum!.commentsCount / 5);
        showToast("Comment added!");
      })
      .addCase(addComment.rejected, (state, action) => {
        state.error = action.payload as string;
        showToast(state.error, "destructive");
      })

      /* -------------------------
       * UPDATE COMMENT
       * ------------------------- */
      .addCase(updateComment.fulfilled, (state, action) => {
        state.comments = state.comments.map((c) =>
          c.commentId === action.payload.commentId ? action.payload : c,
        );
        showToast("Comment updated");
      })
      .addCase(updateComment.rejected, (state, action) => {
        state.error = action.payload as string;
        showToast(state.error, "destructive");
      })

      /* -------------------------
       * DELETE COMMENT
       * ------------------------- */
      .addCase(deleteComment.fulfilled, (state, action) => {
        state.comments = state.comments.filter(
          (c) => c.commentId !== action.payload.commentId,
        );

        state.forum!.commentsCount--;
        state.totalCommentPages = Math.ceil(state.forum!.commentsCount / 5);

        showToast("Comment deleted");
      })
      .addCase(deleteComment.rejected, (state, action) => {
        state.error = action.payload as string;
        showToast(state.error, "destructive");
      });
  },
});

export default forumSlice.reducer;
