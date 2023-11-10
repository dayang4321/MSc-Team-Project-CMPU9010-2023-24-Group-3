import React, { useMemo, useState } from 'react';
import DefaultLayout from '../layouts/DefaultLayout';
import Head from 'next/head';
import Button from '../components/UI/Button';
import DocViewer, { DocViewerRenderers } from '@cyntler/react-doc-viewer';
import SlideModal from '../components/UI/SlideModal';
import { PencilIcon } from '@heroicons/react/24/outline';
import { useRouter } from 'next/router';

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
          <>
            <Button
              className={`flex-1 px-4 py-2 ${
                activeTab === 1
                  ? 'bg-gray-500 text-white'
                  : 'bg-gray-300 text-gray-700'
              }`}
              onClick={() => setActiveTab(1)}
              text={'Text Customization'}
            />
          </>
          <>
            <Button
              className={`flex-1 px-4 py-2 ${
                activeTab === 1
                  ? 'bg-gray-500 text-white'
                  : 'bg-gray-300 text-gray-700'
              }`}
              onClick={() => setActiveTab(1)}
              text={'Text Colour Customization'}
            />
          </>
          <>
            <Button
              className={`flex-1 px-4 py-2 ${
                activeTab === 1
                  ? 'bg-gray-500 text-white'
                  : 'bg-gray-300 text-gray-700'
              }`}
              onClick={() => setActiveTab(1)}
              text={'Advanced Features'}
            />
          </>
        </div>

        {/* Content based on active tab */}
        <SlideModalContent activeTab={activeTab} />
      </SlideModal>
    </DefaultLayout>
  );
};

export default Reader;
