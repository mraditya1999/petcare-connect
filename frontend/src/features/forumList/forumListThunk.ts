import { createAsyncThunk } from "@reduxjs/toolkit";
import { customFetch } from "@/utils/customFetch";
import { handleError } from "@/utils/helpers";
import { IFetchMyForumsResponse, IForum } from "@/types/forum-types";
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
    let url = `/forums?page=${page}&size=${size}&sortBy=${sortBy}&sortDir=${sortDir}`;

    if (tagSearchTerm.trim()) {
      const tags = tagSearchTerm
        .split(",")
        .map((t) => t.trim())
        .filter(Boolean);
      const tagsQuery = tags
        .map((tag) => `tags=${encodeURIComponent(tag)}`)
        .join("&");

      url = `/forums/search-by-tags?${tagsQuery}&page=${page}&size=${size}&sortBy=${sortBy}&sortDir=${sortDir}`;
    } else if (searchTerm.trim()) {
      url = `/forums/search?keyword=${encodeURIComponent(
        searchTerm,
      )}&page=${page}&size=${size}&sortBy=${sortBy}&sortDir=${sortDir}`;
    }

    const res = await customFetch.get<IFetchForumsResponse>(url);
    return res.data;
  } catch (err) {
    return rejectWithValue(handleError(err));
  }
});

// Fetch featured forums
export const fetchFeaturedForums = createAsyncThunk<
  IForum[],
  void,
  { rejectValue: string }
>("forumList/fetchFeaturedForums", async (_, { rejectWithValue }) => {
  try {
    const res = await customFetch.get<IFetchFeaturedForumsResponse>(
      "forums/top-featured",
    );
    return res.data.data;
  } catch (err) {
    return rejectWithValue(handleError(err));
  }
});

// Create forum
export const createForum = createAsyncThunk<
  ICreateForumResponse,
  ICreateForumParams,
  { rejectValue: string }
>("forumList/createForum", async (payload, { dispatch, rejectWithValue }) => {
  try {
    await customFetch.post("/forums", payload);

    // auto-refresh list & featured
    dispatch(
      fetchForums({
        page: 0,
        size: 10,
        sortBy: "createdAt",
        sortDir: "desc",
        searchTerm: "",
        tagSearchTerm: "",
      }),
    );

    dispatch(fetchFeaturedForums());
  } catch (err) {
    return rejectWithValue(handleError(err));
  }
});

// Fetch my forums
// forumListThunk.ts
export const fetchMyForums = createAsyncThunk<
  IFetchMyForumsResponse,
  { page: number; size: number },
  { rejectValue: string }
>("forumList/fetchMyForums", async ({ page, size }, { rejectWithValue }) => {
  try {
    const res = await customFetch.get<IFetchMyForumsResponse>(
      `/forums/my-forums?page=${page}&size=${size}`,
    );
    return res.data;
  } catch (err) {
    return rejectWithValue(handleError(err));
  }
});
