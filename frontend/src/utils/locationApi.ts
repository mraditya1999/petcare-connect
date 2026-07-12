/* eslint-disable @typescript-eslint/no-explicit-any */
import axios from "axios";

export type CountryInfo = {
  country: string;
  cities?: string[];
};

export type StateEntry = {
  name: string;
};

const COUNTRIESNOW_BASE = "https://countriesnow.space/api/v0.1";
const NOMINATIM_BASE = "https://nominatim.openstreetmap.org/search";

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

  if (payload.data?.states) {
    return (payload.data.states || [])
      .map((x: StateEntry) => x.name)
      .filter(Boolean);
  }

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

export async function fetchExternalPincodes(
  country: string,
  state: string,
  city: string,
): Promise<string[]> {
  if (!country || !state || !city) return [];

  try {
    const normalizedCountry = country.trim().toLowerCase();
    if (normalizedCountry === "india") {
      const response = await axios.get(
        `https://api.postalpincode.in/postoffice/${encodeURIComponent(city)}`,
      );
      const results = Array.isArray(response.data) ? response.data : [];
      const pincodes = results.flatMap((item: any) => {
        const offices = Array.isArray(item?.PostOffice) ? item.PostOffice : [];
        return offices
          .map((office: any) => office?.Pincode)
          .filter(
            (value: string | undefined) =>
              Boolean(value) && /^\d{4,6}$/.test(String(value)),
          );
      });

      return Array.from(new Set(pincodes.map((value: string) => String(value))));
    }
  } catch (error) {
    console.error("Failed to fetch pincode data from postal service", error);
  }

  try {
    const response = await axios.get(NOMINATIM_BASE, {
      params: {
        city,
        state,
        country,
        format: "jsonv2",
        addressdetails: 1,
        limit: 10,
      },
      headers: {
        "Accept-Language": "en",
        "User-Agent": "PetcareConnect/1.0",
      },
    });

    const results = Array.isArray(response.data) ? response.data : [];
    const pincodes = results
      .map((item: any) => item.address?.postcode)
      .filter(
        (value: string | undefined) =>
          Boolean(value) && /^\d{4,6}$/.test(String(value)),
      )
      .map((value: string) => String(value));

    return Array.from(new Set(pincodes));
  } catch (error) {
    console.error("Failed to fetch pincode data from live geocoding service", error);
    return [];
  }
}
