import React from 'react';
import {
  Header,
  HeaderName,
  HeaderNavigation,
  HeaderMenuItem,
  SkipToContent,
} from 'carbon-components-react/lib/components/UIShell';
import {Link, LinkProps} from 'react-router-dom';

const TutorialHeader = () => (
  <Header aria-label="Carbon Tutorial">
    <SkipToContent />
    <HeaderName<LinkProps> element={Link} to="/" prefix="JMS">
      Workbench
    </HeaderName>
    <HeaderNavigation aria-label="Carbon Tutorial">
      <HeaderMenuItem<LinkProps> element={Link} to="/repos">
        Repositories
      </HeaderMenuItem>
    </HeaderNavigation>
  </Header>
);

export default TutorialHeader;
