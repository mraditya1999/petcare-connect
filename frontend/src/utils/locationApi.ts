/* eslint-disable @typescript-eslint/no-explicit-any */
import axios from "axios";

export type CountryInfo = {
  country: string;
  cities?: string[];
};

export type StateEntry = {
  name: string;
};

export type IsoEntry = {
  name: string;
  iso2: string;
  iso3?: string;
};

const COUNTRIESNOW_BASE = "https://countriesnow.space/api/v0.1";

const externalFetch = axios.create({
  baseURL: COUNTRIESNOW_BASE,
  withCredentials: false,
});

async function fetchJson(
  url: string,
  options?: {
    method?: "GET" | "POST";
    data?: any;
    headers?: Record<string, string>;
  },
): Promise<any> {
  const response = await externalFetch.request({
    url,
    method: options?.method || "GET",
    data: options?.data,
    headers: options?.headers,
  });
  return response.data;
}

export async function fetchExternalCountries(): Promise<string[]> {
  const payload = await fetchJson("/countries");
  if (payload?.error) return [];
  return (payload.data || [])
    .map((item: CountryInfo) => item.country)
    .filter(Boolean);
}

export async function fetchExternalStates(country: string): Promise<string[]> {
  if (!country) return [];
  const payload = await fetchJson("/countries/states", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    data: { country },
  });

  if (payload?.error) return [];

  // Some responses return { data: { name, states: [ {name} ] } }
  if (payload.data?.states) {
    return (payload.data.states || [])
      .map((x: StateEntry) => x.name)
      .filter(Boolean);
  }

  // fallback if direct list
  if (Array.isArray(payload.data)) {
    return payload.data.map((x: any) => x.name || x).filter(Boolean);
  }

  return [];
}

export async function fetchExternalCities(
  country: string,
  state: string,
): Promise<string[]> {
  if (!country || !state) return [];
  const payload = await fetchJson("/countries/state/cities", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    data: { country, state },
  });

  if (payload?.error) return [];
  if (Array.isArray(payload.data)) {
    return payload.data.filter(Boolean);
  }
  return [];
}

async function fetchIsoList(): Promise<IsoEntry[]> {
  const payload = await fetchJson(`${COUNTRIESNOW_BASE}/countries/iso`);
  if (payload?.error) return [];
  return payload.data || [];
}

export async function fetchExternalPincodes(
  country: string,
  state: string,
  city: string,
): Promise<string[]> {
  if (!country || !city) return [];

  const isoList = await fetchIsoList();
  const matched = isoList.find(
    (item) => item.name?.toLowerCase() === country.toLowerCase(),
  );
  if (!matched?.iso2) return [];

  const iso2 = matched.iso2.toLowerCase();
  const url = `https://api.zippopotam.us/${iso2}/${encodeURIComponent(city)}`;

  try {
    const payload = await fetchJson(url);
    if (!payload?.places || !Array.isArray(payload.places)) return [];

    return Array.from(
      new Set(
        payload.places.map((place: any) => place["post code"]).filter(Boolean),
      ),
    );
  } catch (error) {
    // not all countries are supported by zippopotam.us, so retry with pincode input option
    return [];
  }
}
