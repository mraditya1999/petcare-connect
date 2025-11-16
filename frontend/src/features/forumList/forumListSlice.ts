// forumListSlice.ts
import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import {
  fetchForums,
  fetchFeaturedForums,
  createForum,
} from "./forumListThunk";
import { IForum } from "@/types/forum-types";

export interface IForumState {
  forums: IForum[];
  featuredForums: IForum[];

  page: number;
  size: number;
  totalPages: number;
  totalElements: number;

  sortBy: string;
  sortDir: "asc" | "desc";
  searchTerm: string;
  tagSearchTerm: string;

  loading: boolean;
  error: string | null;
}

const initialState: IForumState = {
  forums: [],
  featuredForums: [],

  page: 0,
  size: 5,
  totalPages: 0,
  totalElements: 0,

  sortBy: "createdAt",
  sortDir: "desc",
  searchTerm: "",
  tagSearchTerm: "",

  loading: false,
  error: null,
};

const forumListSlice = createSlice({
  name: "forumList",
  initialState,
  reducers: {
    setPage(state, action: PayloadAction<number>) {
      state.page = action.payload;
    },
    setSortBy(state, action: PayloadAction<string>) {
      state.sortBy = action.payload;
    },
    setSortDir(state, action: PayloadAction<"asc" | "desc">) {
      state.sortDir = action.payload;
    },
    setSearchTerm(state, action: PayloadAction<string>) {
      state.searchTerm = action.payload;
      state.page = 0;
    },
    setTagSearchTerm(state, action: PayloadAction<string>) {
      state.tagSearchTerm = action.payload;
      state.page = 0;
    },
    setSize(state, action: PayloadAction<number>) {
      state.size = action.payload;
    },
    clearError(state) {
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      // Fetch Forums
      .addCase(fetchForums.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchForums.fulfilled, (state, action) => {
        state.loading = false;
        state.forums = action.payload.content;
        state.totalPages = action.payload.page.totalPages;
        state.totalElements = action.payload.page.totalElements;
      })
      .addCase(fetchForums.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Failed to load forums";
      })

      // Featured Forums
      .addCase(fetchFeaturedForums.fulfilled, (state, action) => {
        state.featuredForums = action.payload;
      })

      // Create Forum
      .addCase(createForum.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(createForum.fulfilled, (state) => {
        state.loading = false;
      })
      .addCase(createForum.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Failed to create forum";
      });
  },
});

export const {
  setPage,
  setSortBy,
  setSortDir,
  setSearchTerm,
  setTagSearchTerm,
  setSize,
  clearError,
} = forumListSlice.actions;

export default forumListSlice.reducer;
