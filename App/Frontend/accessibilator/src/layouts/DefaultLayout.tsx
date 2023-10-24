import React, { FC } from 'react';

interface LayoutProps {
  children: React.ReactNode;
}

const DefaultLayout: FC<LayoutProps> = ({ children }) => {
  return (
    <div className="min-h-screen flex flex-col">
      <nav className="px-8 py-5">
        <div className="text-2xl uppercase font-extrabold">Accessibilator</div>
      </nav>
      {children}
      <footer className="p-5">Footer</footer>
    </div>
  );
};

export default DefaultLayout;
