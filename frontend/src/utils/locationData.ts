export interface CityInfo {
  name: string;
  pincodes: string[];
}

export interface StateInfo {
  name: string;
  cities: CityInfo[];
}

export interface CountryInfo {
  name: string;
  states: StateInfo[];
}

export const locationData: CountryInfo[] = [
  {
    name: "India",
    states: [
      {
        name: "Karnataka",
        cities: [
          { name: "Bengaluru", pincodes: ["560001", "560002", "560005"] },
          { name: "Mysore", pincodes: ["570001", "570002", "570010"] },
        ],
      },
      {
        name: "Maharashtra",
        cities: [
          { name: "Mumbai", pincodes: ["400001", "400002", "400009"] },
          { name: "Pune", pincodes: ["411001", "411002", "411018"] },
        ],
      },
    ],
  },
  {
    name: "United States",
    states: [
      {
        name: "California",
        cities: [
          { name: "Los Angeles", pincodes: ["90001", "90002", "90003"] },
          { name: "San Francisco", pincodes: ["94102", "94103"] },
        ],
      },
      {
        name: "Texas",
        cities: [
          { name: "Houston", pincodes: ["77001", "77002"] },
          { name: "Austin", pincodes: ["73301", "73344"] },
        ],
      },
    ],
  },
];
