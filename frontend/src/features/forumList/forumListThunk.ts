import { createAsyncThunk } from "@reduxjs/toolkit";
import { customFetch } from "@/utils/customFetch";
import { handleError } from "@/utils/helpers";
import { IFetchMyForumsResponse } from "@/types/forum-types";
import { RootState } from "@/app/store";
import {
  IFetchForumsParams,
  IFetchForumsResponse,
  IFetchFeaturedForumsResponse,
  ICreateForumParams,
  ICreateForumResponse,
} from "@/types/forum-thunk-types";

// Fetch forums
export const fetchForums = createAsyncThunk<
  IFetchForumsResponse,
  IFetchForumsParams,
  { rejectValue: string }
>("forumList/fetchForums", async (params, { rejectWithValue }) => {
  const { page, size, sortBy, sortDir, searchTerm, tagSearchTerm } = params;
  try {
    let url = `/forums?pageNumber=${page}&pageSize=${size}&sortBy=${sortBy}&sortOrder=${sortDir}`;

    if (tagSearchTerm.trim()) {
      const tags = tagSearchTerm
        .split(",")
        .map((t) => t.trim())
        .filter(Boolean);
      const tagsQuery = tags
        .map((tag) => `tags=${encodeURIComponent(tag)}`)
        .join("&");

      url = `/forums/search?${tagsQuery}&pageNumber=${page}&pageSize=${size}&sortBy=${sortBy}&sortOrder=${sortDir}`;
    } else if (searchTerm.trim()) {
      url = `/forums/search?keyword=${encodeURIComponent(
        searchTerm,
      )}&pageNumber=${page}&pageSize=${size}&sortBy=${sortBy}&sortOrder=${sortDir}`;
    }

    const res = await customFetch.get<IFetchForumsResponse>(url);
    return {
      message: res.data.message,
      data: {
        forums: res.data.data?.forums ?? [],
        pageNumber: res.data.data?.pageNumber ?? page,
        pageSize: res.data.data?.pageSize ?? size,
        totalElements: res.data.data?.totalElements ?? 0,
        totalPages: res.data.data?.totalPages ?? 1,
        lastPage: res.data.data?.lastPage ?? true,
      },
    };
  } catch (err) {
    return rejectWithValue(handleError(err));
  }
});

// Fetch featured forums
export const fetchFeaturedForums = createAsyncThunk<
  IFetchFeaturedForumsResponse,
  void,
  { rejectValue: string }
>("forumList/fetchFeaturedForums", async (_, { rejectWithValue }) => {
  try {
    const res = await customFetch.get<IFetchFeaturedForumsResponse>(
      "/forums/top-featured?pageNumber=0&pageSize=5",
    );
    return {
      message: res.data.message,
      data: res.data.data ?? [],
    };
  } catch (err) {
    return rejectWithValue(handleError(err));
  }
});

// Create forum
export const createForum = createAsyncThunk<
  ICreateForumResponse,
  ICreateForumParams,
  { rejectValue: string; state: RootState }
>("forumList/createForum", async (payload, { dispatch, rejectWithValue, getState }) => {
  try {
    await customFetch.post("/forums", payload);

    // auto-refresh list & featured using current list settings
    const state = getState();
    const { size, sortBy, sortDir, searchTerm, tagSearchTerm } = state.forumList;
    dispatch(
      fetchForums({
        page: 0,
        size,
        sortBy,
        sortDir,
        searchTerm,
        tagSearchTerm,
      }),
    );

    dispatch(fetchFeaturedForums());
  } catch (err) {
    return rejectWithValue(handleError(err));
  }
});

// Fetch my forums
export const fetchMyForums = createAsyncThunk<
  IFetchMyForumsResponse,
  { page: number; size: number },
  { rejectValue: string }
>("forumList/fetchMyForums", async ({ page, size }, { rejectWithValue }) => {
  try {
    const res = await customFetch.get<IFetchMyForumsResponse>(
      `/forums/my-forums?pageNumber=${page}&pageSize=${size}`,
    );
    return {
      message: res.data.message,
      data: {
        forums: res.data.data?.forums ?? [],
        pageNumber: res.data.data?.pageNumber ?? page,
        pageSize: res.data.data?.pageSize ?? size,
        totalElements: res.data.data?.totalElements ?? 0,
        totalPages: res.data.data?.totalPages ?? 1,
        lastPage: res.data.data?.lastPage ?? true,
      },
    } as IFetchMyForumsResponse;
  } catch (err) {
    return rejectWithValue(handleError(err));
  }
});
