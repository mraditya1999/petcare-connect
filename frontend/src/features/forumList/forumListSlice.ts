import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { IForumListState } from "@/types/forum-types";
import {
  fetchForums,
  fetchFeaturedForums,
  createForum,
  fetchMyForums,
} from "./forumListThunk";

const initialState: IForumListState = {
  forums: [],
  featuredForums: [],
  myForums: [],
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
  featuredLoading: false,
  featuredError: null,
  myForumsPage: 0,
  myForumsTotalPages: 0,
  myForumsTotalElements: 0,
  myForumsLoading: false,
  myForumsError: null,
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
    setMyForumsPage(state, action: PayloadAction<number>) {
      state.myForumsPage = action.payload;
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
      });

    // Featured Forums
    builder
      .addCase(fetchFeaturedForums.pending, (state) => {
        state.featuredLoading = true;
        state.featuredError = null;
      })
      .addCase(fetchFeaturedForums.fulfilled, (state, action) => {
        state.featuredLoading = false;
        state.featuredForums = action.payload;
      })
      .addCase(fetchFeaturedForums.rejected, (state, action) => {
        state.featuredLoading = false;
        state.featuredError =
          action.error.message || "Failed to load featured forums";
      });
    // Create Forum
    builder
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
    builder
      .addCase(fetchMyForums.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchMyForums.fulfilled, (state, action) => {
        state.loading = false;
        state.myForums = action.payload.data.content;
        state.myForumsTotalPages = action.payload.data.page.totalPages;
        state.myForumsTotalElements = action.payload.data.page.totalElements;
      })
      .addCase(fetchMyForums.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Failed to load my forums";
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
  setMyForumsPage,
} = forumListSlice.actions;

export default forumListSlice.reducer;
