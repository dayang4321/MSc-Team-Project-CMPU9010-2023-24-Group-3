import Head from 'next/head';
import React, { useState } from 'react';
import DefaultLayout from '../layouts/DefaultLayout';

import Button from '../components/UI/Button';
import MyToggle from '../components/UI/MyToogle';
import { useRouter } from 'next/router';
import axiosInit from '../services/axios';

type PrimaryFix =
  | 'fontStyle'
  | 'fontSize'
  | 'interSpacing'
  | 'lineSpacing'
  | 'contrast'
  | 'italics';

export default function IdentifiedFixes() {
  const [showMessage, setShowMessage] = useState(false);

  const [isModifyLoading, setIsModifyLoading] = useState(false);
  const router = useRouter();
  const { doc_key } = router.query;

  const [choicesObj, setChoicesObj] = useState<Record<PrimaryFix, boolean>>({
    fontStyle: true,
    fontSize: true,
    interSpacing: true,
    lineSpacing: true,
    contrast: false,
    italics: false,
  });

  const setChoiceHandler = (choice: PrimaryFix) => {
    setChoicesObj((s) => ({
      ...s,
      [choice]: !s[choice],
    }));
  };

  const onReviewConfirm = () => {
    setIsModifyLoading(true);

    axiosInit
      .get('/modifyFile', {
        params: {
          filename: doc_key,
        },
      })
      .then((res) => {
        console.log(res);

        //  router.push('/accessibility-review');
        router.push({
          pathname: '/reader',
          query: { doc_url: res.data.url },
        });
      })
      .catch((err) => {
        console.log(err);
      })
      .finally(() => {
        setIsModifyLoading(false);
      });
  };

  return (
    <DefaultLayout>
      <Head>
        <title>Accessibilator</title>
        <link rel="icon" href="/favicon.ico" />
      </Head>

      <main className="flex flex-col text-center justify-center items-center py-16 pb-8 flex-1 bg-slate-50 text-gray-900">
        <div className="max-w-4xl text-center">
          <h1 className="text-5xl  font-bold mb-11">
            The document you uploaded has been processed and modified
          </h1>
          <h3 className="text-2xl">
            Here are few modifications we made to the document to make it more
            accessible and readable by you
          </h3>

          <div className="border rounded-md border-gray-600 p-7 mt-12 divide-y divide-solid">
            <div className="justify-between flex items-center py-5">
              <p>Font style changed</p>
              <MyToggle
                checked={choicesObj.fontStyle}
                onChange={() => setChoiceHandler('fontStyle')}
              />
            </div>
            <div className="justify-between flex items-center py-5">
              <p>Font size increased</p>
              <MyToggle
                checked={choicesObj.fontSize}
                onChange={() => setChoiceHandler('fontSize')}
              />
            </div>
            <div className="justify-between flex items-center py-5">
              <p>Inter-letter spacing increased</p>
              <MyToggle
                checked={choicesObj.interSpacing}
                onChange={() => setChoiceHandler('interSpacing')}
              />
            </div>
            <div className="justify-between flex items-center py-5">
              <p>Line spacing increased</p>
              <MyToggle
                checked={choicesObj.lineSpacing}
                onChange={() => setChoiceHandler('lineSpacing')}
              />
            </div>
            <div className="justify-between flex items-center py-5">
              <p>Contrast increased</p>
              <MyToggle checked={choicesObj.contrast} />
            </div>
            <div className="justify-between flex items-center py-5">
              <p>Italics removed</p>
              <MyToggle checked={choicesObj.italics} />
            </div>
          </div>
        </div>

        <div className="text-right w-full mt-8 max-w-4xl ">
          <Button
            variant="link"
            className=" text-stone-700 border border-stone-700 mr-10 py-2 px-6 text-base font-medium"
            text={'Back'}
            onClick={() => {
              router.back();
            }}
          />
          <Button
            className="bg-stone-700 text-base py-2 px-6 text-zinc-50 font-medium"
            loading={isModifyLoading}
            text={'Continue'}
            onClick={() => {
              onReviewConfirm();
            }}
          />
        </div>
      </main>
    </DefaultLayout>
  );
}