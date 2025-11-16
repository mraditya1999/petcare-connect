// src/features/forumDetail/forumDetailThunk.ts
import { createAsyncThunk } from "@reduxjs/toolkit";
import { customFetch } from "@/utils/customFetch";
import { handleError } from "@/utils/helpers";
import {
  ISingleForumResponse,
  ICommentListResponse,
  IAddCommentResponse,
  IUpdateCommentResponse,
  ICheckLikeResponse,
  IToggleLikeResponse,
} from "@/types/forum-types";

export const fetchSingleForum = createAsyncThunk<
  ISingleForumResponse,
  string,
  { rejectValue: string }
>("forumDetail/fetchSingleForum", async (forumId, { rejectWithValue }) => {
  try {
    const res = await customFetch.get<ISingleForumResponse>(
      `/forums/${forumId}`,
    );
    return res.data;
  } catch (err) {
    return rejectWithValue(handleError(err));
  }
});

export const fetchComments = createAsyncThunk<
  ICommentListResponse,
  { forumId: string; page?: number; size?: number },
  { rejectValue: string }
>(
  "forumDetail/fetchComments",
  async ({ forumId, page = 0, size = 5 }, { rejectWithValue }) => {
    try {
      const res = await customFetch.get<ICommentListResponse>(
        `/comments/forums/${forumId}?page=${page}&size=${size}`,
      );
      return res.data;
    } catch (err) {
      return rejectWithValue(handleError(err));
    }
  },
);

export const createComment = createAsyncThunk<
  IAddCommentResponse,
  { forumId: string; text: string },
  { rejectValue: string }
>(
  "forumDetail/createComment",
  async ({ forumId, text }, { rejectWithValue }) => {
    try {
      const res = await customFetch.post<IAddCommentResponse>(
        `/comments/forums/${forumId}`,
        { text },
      );
      return res.data;
    } catch (err) {
      return rejectWithValue(handleError(err));
    }
  },
);

export const updateComment = createAsyncThunk<
  IUpdateCommentResponse,
  { commentId: string; text: string },
  { rejectValue: string }
>(
  "forumDetail/updateComment",
  async ({ commentId, text }, { rejectWithValue }) => {
    try {
      const res = await customFetch.put<IUpdateCommentResponse>(
        `/comments/${commentId}`,
        { text },
      );
      return res.data;
    } catch (err) {
      return rejectWithValue(handleError(err));
    }
  },
);

export const deleteComment = createAsyncThunk<
  { commentId: string },
  { commentId: string },
  { rejectValue: string }
>("forumDetail/deleteComment", async ({ commentId }, { rejectWithValue }) => {
  try {
    await customFetch.delete(`/comments/${commentId}`);
    return { commentId };
  } catch (err) {
    return rejectWithValue(handleError(err));
  }
});

export const checkLike = createAsyncThunk<
  ICheckLikeResponse,
  { forumId: string },
  { rejectValue: string }
>("forumDetail/checkLike", async ({ forumId }, { rejectWithValue }) => {
  try {
    const res = await customFetch.get<ICheckLikeResponse>(
      `/likes/forums/${forumId}/check`,
    );
    return res.data;
  } catch (err) {
    return rejectWithValue(handleError(err));
  }
});

export const toggleLike = createAsyncThunk<
  IToggleLikeResponse,
  { forumId: string },
  { rejectValue: string }
>("forumDetail/toggleLike", async ({ forumId }, { rejectWithValue }) => {
  try {
    const res = await customFetch.post<IToggleLikeResponse>(
      `/likes/forums/${forumId}`, // removed /toggle
    );
    return res.data;
  } catch (err) {
    return rejectWithValue(handleError(err));
  }
});
