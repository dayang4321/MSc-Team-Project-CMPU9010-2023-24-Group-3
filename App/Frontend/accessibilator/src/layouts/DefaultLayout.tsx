import React, { FC } from 'react';

interface LayoutProps {
  children: React.ReactNode;
}

const DefaultLayout: FC<LayoutProps> = ({ children }) => {
  return (
    <div className="min-h-screen flex flex-col">
      <nav className="p-5">
        <div>Logo</div>
      </nav>
      {children}
      <footer className="p-5">Footer</footer>
    </div>
  );
};

export default DefaultLayout;
