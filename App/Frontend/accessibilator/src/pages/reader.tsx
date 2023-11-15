import React, { useMemo, useState } from 'react';
import DefaultLayout from '../layouts/DefaultLayout';
import Head from 'next/head';
import Button from '../components/UI/Button';
import DocViewer, { DocViewerRenderers } from '@cyntler/react-doc-viewer';
import SlideModal from '../components/UI/SlideModal';
import { PencilIcon } from '@heroicons/react/24/outline';
import { useRouter } from 'next/router';
import { Tab } from '@headlessui/react';

type Props = {};

const CustomizationPanel = () => {
  // Code for handling text customization state
  const [fontType, setFontType] = useState('Default Font');
  const [fontSize, setFontSize] = useState(16);
  const [lineSpacing, setLineSpacing] = useState(1.5);
  const [letterSpacing, setLetterSpacing] = useState(0);
  const [wordSpacing, setWordSpacing] = useState(false);
  const [alignLayout, setAlignLayout] = useState('left');
  const [italic, setItalic] = useState(true);
  const [underline, setUnderline] = useState(true);

  return (
    <div className='p-4'>
      <div className='mb-4'>
        {/* Font Type Dropdown */}
        <label className='block text-sm font-medium text-gray-700'>
          Font Type
        </label>
        <select
          className='mt-1 w-full rounded-md border border-gray-300 p-2'
          value={fontType}
          onChange={(e) => setFontType(e.target.value)}
        >
          {/* Add font options based on your design */}
          <option value='Default Font'>Default Font</option>
          <option value='Arial'>Arial</option>
          {/* Add more font options as needed */}
        </select>
      </div>

      <div className='mb-4'>
        {/* Font Size Scale */}
        <label className='block text-sm font-medium text-gray-700'>
          Font Size
        </label>
        <input
          type='range'
          min='10'
          max='40'
          step='1'
          value={fontSize}
          onChange={(e) => setFontSize(parseInt(e.target.value))}
          className='w-full'
        />
      </div>

      {/* Add other text customization options using similar patterns */}

      <div className='mb-4'>
        {/* Align Layout Buttons */}
        <label className='block text-sm font-medium text-gray-700'>
          Align Layout
        </label>
        <div className='flex space-x-4'>
          <button
            className={`rounded px-4 py-2 ${
              alignLayout === 'left' ? 'bg-blue-500 text-white' : 'bg-gray-300'
            }`}
            onClick={() => setAlignLayout('left')}
          >
            Left
          </button>
          <button
            className={`rounded px-4 py-2 ${
              alignLayout === 'center'
                ? 'bg-blue-500 text-white'
                : 'bg-gray-300'
            }`}
            onClick={() => setAlignLayout('center')}
          >
            Center
          </button>
          <button
            className={`rounded px-4 py-2 ${
              alignLayout === 'right' ? 'bg-blue-500 text-white' : 'bg-gray-300'
            }`}
            onClick={() => setAlignLayout('right')}
          >
            Right
          </button>
        </div>
      </div>

      <div className='mb-4'>
        {/* Remove Italics Toggle */}
        <label className='block text-sm font-medium text-gray-700'>
          Remove Italics
        </label>
        <input
          type='checkbox'
          checked={!italic}
          onChange={() => setItalic(!italic)}
          className='form-checkbox h-5 w-5 text-blue-500'
        />
      </div>

      <div className='mb-4'>
        {/* Remove Underline Toggle */}
        <label className='block text-sm font-medium text-gray-700'>
          Remove Underline
        </label>
        <input
          type='checkbox'
          checked={!underline}
          onChange={() => setUnderline(!underline)}
          className='form-checkbox h-5 w-5 text-blue-500'
        />
      </div>
    </div>
  );
};

const AdvancedFeaturesTab = () => {
  // Code for handling advanced features state
  // Add your advanced features UI elements here
  return (
    <div className='p-4'>{/* Add UI elements for advanced features */}</div>
  );
};

const SlideModalContent = ({ activeTab }) => {
  return (
    <div>
      {activeTab === 1 && <CustomizationPanel />}
      {activeTab === 2 && (
        <div>{/* Add Text Color Customization UI here */}</div>
      )}
      {activeTab === 3 && <AdvancedFeaturesTab />}
    </div>
  );
};

const Reader = (props: Props) => {
  const [slideModalOpen, setSlideModalOpen] = useState(false);
  const [activeTab, setActiveTab] = useState(1); // Add this line

  const router = useRouter();
  const { doc_url } = router.query;

  const docUri = Array.isArray(doc_url) ? doc_url[0] : doc_url;

  const memoisedReader = useMemo(() => {
    return (
      <DocViewer
        requestHeaders={{
          'Access-Control-Allow-Origin': '*',
        }}
        prefetchMethod='GET'
        documents={[
          {
            uri: docUri,
          },
        ]}
        pluginRenderers={DocViewerRenderers}
        style={{
          flex: 1,
          backgroundColor: 'red',
        }}
        config={{}}
        className='my-doc-viewer-style'
      />
    );
  }, [docUri]);

  return (
    <DefaultLayout variant='dark'>
      <Head>
        <title>Accessibilator</title>
        <link rel='icon' href='/favicon.ico' />
      </Head>
      <nav className='flex items-center justify-between border-b border-stone-800 px-20 py-3'>
        <div>
          <Button
            role='navigation'
            variant='link'
            className=' px-6 py-2 text-base font-medium text-stone-700'
            text={'Back to homepage'}
            onClick={() => {
              router.push('/');
            }}
          />
        </div>

        <div>
          <Button
            role='navigation'
            variant='link'
            className=' mr-10 border border-stone-700 px-6 py-2 text-base font-medium text-stone-700'
            text={'Upload New Document'}
            onClick={() => {
              router.push('/');
            }}
          />
          <Button
            className='bg-stone-700 px-6 py-2 text-base font-medium text-zinc-50'
            text={'Export'}
            onClick={() => {}}
          />
        </div>
      </nav>
      <main className='flex flex-1 bg-slate-50 py-16 pb-8 text-gray-900'>
        <div className='flex flex-1 flex-col items-stretch border border-stone-800'>
          {memoisedReader}
        </div>
        <div className='flex flex-col self-stretch px-3'>
          <Button
            className='mt-10 inline-flex flex-col gap-y-3 rounded-3xl bg-stone-700 px-6 py-9 text-base font-medium text-zinc-50'
            text={'Edit'}
            icon={<PencilIcon className='h-6 w-6' />}
            onClick={() => {
              setSlideModalOpen(true);
            }}
          />
        </div>
      </main>
      {/* <SlideModal
        open={slideModalOpen}
        setOpen={setSlideModalOpen}
        title={'Edit Document'}
      >
        <div>Customisation Panel</div>
      </SlideModal> */}

      <SlideModal
        open={slideModalOpen}
        setOpen={setSlideModalOpen}
        title={'Edit Document'}
      >
        {/* Tab navigation */}
        <div className='mb-4 flex'>
          <div className='border-b border-gray-200 dark:border-gray-700'>
            <ul className='-mb-px flex flex-wrap text-center text-sm font-medium text-gray-500 dark:text-gray-400'>
              <li className='me-2'>
                <a
                  href='#'
                  className='group inline-flex items-center justify-center rounded-t-lg border-b-2 border-transparent p-4 hover:border-gray-300 hover:text-gray-600 dark:hover:text-gray-300'
                >
                  <svg
                    className='me-2 h-4 w-4 text-gray-400 group-hover:text-gray-500 dark:text-gray-500 dark:group-hover:text-gray-300'
                    aria-hidden='true'
                    xmlns='http://www.w3.org/2000/svg'
                    fill='currentColor'
                    viewBox='0 0 20 20'
                  >
                    <path d='M10 0a10 10 0 1 0 10 10A10.011 10.011 0 0 0 10 0Zm0 5a3 3 0 1 1 0 6 3 3 0 0 1 0-6Zm0 13a8.949 8.949 0 0 1-4.951-1.488A3.987 3.987 0 0 1 9 13h2a3.987 3.987 0 0 1 3.951 3.512A8.949 8.949 0 0 1 10 18Z' />
                  </svg>
                  Text customization
                </a>
              </li>
              <li className='me-2'>
                <a
                  href='#'
                  className='active group inline-flex items-center justify-center rounded-t-lg border-b-2 border-blue-600 p-4 text-blue-600 dark:border-blue-500 dark:text-blue-500'
                  aria-current='page'
                >
                  <svg
                    className='me-2 h-4 w-4 text-blue-600 dark:text-blue-500'
                    aria-hidden='true'
                    xmlns='http://www.w3.org/2000/svg'
                    fill='currentColor'
                    viewBox='0 0 18 18'
                  >
                    <path d='M6.143 0H1.857A1.857 1.857 0 0 0 0 1.857v4.286C0 7.169.831 8 1.857 8h4.286A1.857 1.857 0 0 0 8 6.143V1.857A1.857 1.857 0 0 0 6.143 0Zm10 0h-4.286A1.857 1.857 0 0 0 10 1.857v4.286C10 7.169 10.831 8 11.857 8h4.286A1.857 1.857 0 0 0 18 6.143V1.857A1.857 1.857 0 0 0 16.143 0Zm-10 10H1.857A1.857 1.857 0 0 0 0 11.857v4.286C0 17.169.831 18 1.857 18h4.286A1.857 1.857 0 0 0 8 16.143v-4.286A1.857 1.857 0 0 0 6.143 10Zm10 0h-4.286A1.857 1.857 0 0 0 10 11.857v4.286c0 1.026.831 1.857 1.857 1.857h4.286A1.857 1.857 0 0 0 18 16.143v-4.286A1.857 1.857 0 0 0 16.143 10Z' />
                  </svg>
                  Text colour customization
                </a>
              </li>
              <li>
                <a className='inline-block cursor-not-allowed rounded-t-lg p-4 text-gray-400 dark:text-gray-500'>
                  Advanced Features
                </a>
              </li>
            </ul>
          </div>
        </div>

        {/* Content based on active tab */}
        <SlideModalContent activeTab={activeTab} />
      </SlideModal>
    </DefaultLayout>
  );
};

export default Reader;
