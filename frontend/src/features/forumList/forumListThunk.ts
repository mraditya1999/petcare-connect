// forumListThunk.ts
import { createAsyncThunk } from "@reduxjs/toolkit";
import { customFetch } from "@/utils/customFetch";
import { IForum, IFeaturedForumResponse } from "@/types/forum-types";
import { IPageResponse } from "@/types/pagination-types";
import { handleError } from "@/utils/helpers";

export const fetchForums = createAsyncThunk<
  IPageResponse<IForum>,
  {
    page: number;
    size: number;
    sortBy: string;
    sortDir: "asc" | "desc";
    searchTerm: string;
    tagSearchTerm: string;
  },
  { rejectValue: string }
>(
  "forumList/fetchForums",
  async (
    { page, size, sortBy, sortDir, searchTerm, tagSearchTerm },
    { rejectWithValue },
  ) => {
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

      const res = await customFetch.get<IPageResponse<IForum>>(url);
      console.log(res.data);
      return res.data;
    } catch (err) {
      return rejectWithValue(handleError(err));
    }
  },
);

export const fetchFeaturedForums = createAsyncThunk<
  IForum[],
  void,
  { rejectValue: string }
>("forumList/fetchFeaturedForums", async (_, { rejectWithValue }) => {
  try {
    const res = await customFetch.get<IFeaturedForumResponse>(
      "forums/top-featured",
    );
    return res.data.data;
  } catch (err) {
    return rejectWithValue(handleError(err));
  }
});

export const createForum = createAsyncThunk<
  void,
  { title: string; content: string; tags: string[] },
  { rejectValue: string }
>("forumList/createForum", async (payload, { dispatch, rejectWithValue }) => {
  try {
    await customFetch.post("/forums", payload);

    // ⚡ auto-refresh list & featured
    // Here we use default params for refreshing
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
