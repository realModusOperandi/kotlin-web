export interface Header {
  key: string
  header: string
}

export interface LinkProps {
  url: string;
  homepageUrl: string;
}

export interface RepoTableProps {
  rows: any[];
  headers: Header[];
}
