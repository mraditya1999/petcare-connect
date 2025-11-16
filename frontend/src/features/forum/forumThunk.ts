// src/features/forum/forumThunk.ts
import { createAsyncThunk } from "@reduxjs/toolkit";
import { customFetch } from "@/utils/customFetch";
import { handleError } from "@/utils/helpers";
import {
  IForum,
  IComment,
  CreateCommentParams,
  UpdateCommentParams,
  ICommentsPageData,
} from "@/types/forum-types";

export const fetchSingleForum = createAsyncThunk<
  IForum,
  string,
  { rejectValue: string }
>("forum/fetchSingleForum", async (forumId, { rejectWithValue }) => {
  try {
    const response = await customFetch.get(`/forums/${forumId}`);
    return response.data.data;
  } catch (error) {
    return rejectWithValue(handleError(error));
  }
});

// ⬇️ NEW THUNK: Fetch paginated comments
export const fetchComments = createAsyncThunk<
  ICommentsPageData,
  { forumId: string; page?: number },
  { rejectValue: string }
>("forum/fetchComments", async ({ forumId, page = 0 }, { rejectWithValue }) => {
  try {
    const response = await customFetch.get(
      `/comments/forum/${forumId}?page=${page}`,
    );
    return response.data.data;
  } catch (error) {
    return rejectWithValue(handleError(error));
  }
});

export const addComment = createAsyncThunk<
  IComment,
  CreateCommentParams,
  { rejectValue: string }
>("forum/addComment", async ({ forumId, text }, { rejectWithValue }) => {
  try {
    const response = await customFetch.post(`/comments/forums/${forumId}`, {
      text,
    });
    return response.data.data;
  } catch (error) {
    return rejectWithValue(handleError(error));
  }
});

export const updateComment = createAsyncThunk<
  IComment,
  UpdateCommentParams,
  { rejectValue: string }
>("forum/updateComment", async ({ commentId, text }, { rejectWithValue }) => {
  try {
    const response = await customFetch.put(`/comments/${commentId}`, {
      text,
    });
    return response.data.data;
  } catch (error) {
    return rejectWithValue(handleError(error));
  }
});

export const deleteComment = createAsyncThunk<
  { commentId: string },
  string,
  { rejectValue: string }
>("forum/deleteComment", async (commentId, { rejectWithValue }) => {
  try {
    await customFetch.delete(`/comments/${commentId}`);
    return { commentId };
  } catch (error) {
    return rejectWithValue(handleError(error));
  }
});
