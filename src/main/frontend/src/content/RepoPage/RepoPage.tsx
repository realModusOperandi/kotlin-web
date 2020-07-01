import React, {useState} from 'react';
import RepoTable from './RepoTable';
import { gql } from 'apollo-boost';
import {Query, QueryResult} from 'react-apollo';
import { Link, DataTableSkeleton, Pagination } from 'carbon-components-react';
import {Header} from "./types";
import {LinkProps} from "./types";

const REPO_QUERY = gql`
  query REPO_QUERY {
    # Let's use carbon as our organization
    organization(login: "carbon-design-system") {
      # We'll grab all the repositories in one go. To load more resources
      # continuously, see the advanced topics.
      repositories(first: 75, orderBy: { field: UPDATED_AT, direction: DESC }) {
        totalCount
        nodes {
          url
          homepageUrl
          issues(filterBy: { states: OPEN }) {
            totalCount
          }
          stargazers {
            totalCount
          }
          releases(first: 1) {
            totalCount
            nodes {
              name
            }
          }
          name
          updatedAt
          createdAt
          description
          id
        }
      }
    }
  }
`;

const headers: Header[] = [
  {
    key: 'name',
    header: 'Name',
  },
  {
    key: 'createdAt',
    header: 'Created',
  },
  {
    key: 'updatedAt',
    header: 'Updated',
  },
  {
    key: 'issueCount',
    header: 'Open Issues',
  },
  {
    key: 'stars',
    header: 'Stars',
  },
  {
    key: 'links',
    header: 'Links',
  },
];

const LinkList = (props: LinkProps) => (
  <ul style={{ display: 'flex' }}>
    <li>
      <Link href={props.url}>GitHub</Link>
    </li>
    {props.homepageUrl && (
      <li>
        <span>&nbsp;|&nbsp;</span>
        <Link href={props.homepageUrl}>Homepage</Link>
      </li>
    )}
  </ul>
);

const getRowItems = (rows: any[]): any[] =>
  rows.map(row => ({
    ...row,
    key: row.id,
    stars: row.stargazers.totalCount,
    issueCount: row.issues.totalCount,
    createdAt: new Date(row.createdAt).toLocaleDateString(),
    updatedAt: new Date(row.updatedAt).toLocaleDateString(),
    links: <LinkList url={row.url} homepageUrl={row.homepageUrl} />,
  }));

const RepoPage = () => {
  const [totalItems, setTotalItems] = useState(0);
  const [firstRowIndex, setFirstRowIndex] = useState(0);
  const [currentPageSize, setCurrentPageSize] = useState(10);

  return (
    <div className="bx--grid bx--grid--full-width bx--grid--no-gutter repo-page">
      <div className="bx--row repo-page__r1">
        <div className="bx--col-lg-16">
          <Query query={REPO_QUERY}>
            {(result: QueryResult) => {
              // Wait for the request to complete
              if (result.loading)
                return (
                  <DataTableSkeleton
                    columnCount={headers.length + 1}
                    rowCount={10}
                  />
                );

              // Something went wrong with the data fetching
              if (result.error) return `Error! ${result.error.message}`;

              // If we're here, we've got our data!
              const { repositories } = result.data.organization;
              setTotalItems(repositories.totalCount);
              const rows = getRowItems(repositories.nodes);

              return (
                <>
                  <RepoTable
                    headers={headers}
                    rows={rows.slice(
                      firstRowIndex,
                      firstRowIndex + currentPageSize
                    )}
                  />
                  <Pagination
                    totalItems={totalItems}
                    backwardText="Previous page"
                    forwardText="Next page"
                    pageSize={currentPageSize}
                    pageSizes={[5, 10, 15, 25]}
                    itemsPerPageText="Items per page"
                    onChange={({ page, pageSize }) => {
                      if (pageSize !== currentPageSize) {
                        setCurrentPageSize(pageSize);
                      }
                      setFirstRowIndex(pageSize * (page - 1));
                    }}
                  />
                </>
              );
            }}
          </Query>
        </div>
      </div>
    </div>
  );
};

export default RepoPage;
