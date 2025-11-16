import { createAsyncThunk } from "@reduxjs/toolkit";
import { customFetch } from "@/utils/customFetch";
import { handleError } from "@/utils/helpers";
import {
  IFetchSingleForumParams,
  IFetchSingleForumResponse,
  IFetchCommentsParams,
  IFetchCommentsResponse,
  ICreateCommentParams,
  ICreateCommentResponse,
  IUpdateCommentParams,
  IUpdateCommentResponse,
  IDeleteCommentParams,
  IDeleteCommentResponse,
  ICheckLikeParams,
  ICheckLikeResponse,
  IToggleLikeParams,
  IToggleLikeResponse,
} from "@/types/forum-thunk-types";

export const fetchSingleForum = createAsyncThunk<
  IFetchSingleForumResponse,
  IFetchSingleForumParams["forumId"],
  { rejectValue: string }
>("forumDetail/fetchSingleForum", async (forumId, { rejectWithValue }) => {
  try {
    const res = await customFetch.get<IFetchSingleForumResponse>(
      `/forums/${forumId}`,
    );
    return res.data;
  } catch (err) {
    return rejectWithValue(handleError(err));
  }
});

export const fetchComments = createAsyncThunk<
  IFetchCommentsResponse,
  IFetchCommentsParams,
  { rejectValue: string }
>(
  "forumDetail/fetchComments",
  async ({ forumId, page = 0, size = 5 }, { rejectWithValue }) => {
    try {
      const res = await customFetch.get<IFetchCommentsResponse>(
        `/comments/forums/${forumId}?page=${page}&size=${size}`,
      );
      return res.data;
    } catch (err) {
      return rejectWithValue(handleError(err));
    }
  },
);

export const createComment = createAsyncThunk<
  ICreateCommentResponse,
  ICreateCommentParams,
  { rejectValue: string }
>(
  "forumDetail/createComment",
  async ({ forumId, text }, { rejectWithValue }) => {
    try {
      const res = await customFetch.post<ICreateCommentResponse>(
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
  IUpdateCommentParams,
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
  IDeleteCommentResponse,
  IDeleteCommentParams,
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
  ICheckLikeParams,
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
  IToggleLikeParams,
  { rejectValue: string }
>("forumDetail/toggleLike", async ({ forumId }, { rejectWithValue }) => {
  try {
    const res = await customFetch.post<IToggleLikeResponse>(
      `/likes/forums/${forumId}`,
    );
    return res.data;
  } catch (err) {
    return rejectWithValue(handleError(err));
  }
});
