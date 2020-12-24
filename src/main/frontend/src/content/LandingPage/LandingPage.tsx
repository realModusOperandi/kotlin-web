import React, { useState, useEffect } from 'react';
import {
  Breadcrumb,
  BreadcrumbItem,
  Button,
  Tabs,
  Tab,
  TextInput,
  ToastNotification, ToastNotificationProps, TabProps
} from 'carbon-components-react';
import axios from 'axios';

const props = {
  tabs: {
    selected: 0,
    triggerHref: '#',
    role: 'navigation',
  },
  tab: {
    handleTabAnchorFocus: () => {},
    handleTabClick: () => {},
    handleTabKeyDown: () => {},
    href: '#',
    role: 'presentation',
    tabIndex: 0,
  },
};

const LandingPage = () => {
  const [testMessage, setTestMessage] = useState('');
  const [toastProps, setToastProps] = useState<ToastNotificationProps[]>([]);

  const arePropsEqual = (a: any, b: any) => {
    // Create arrays of property names
    let aProps = Object.getOwnPropertyNames(a);
    let bProps = Object.getOwnPropertyNames(b);

    // If number of properties is different,
    // objects are not equivalent
    if (aProps.length !== bProps.length) {
      return false;
    }

    for (let i = 0; i < aProps.length; i++) {
      let propName = aProps[i];

      // If values of same property are not equal,
      // objects are not equivalent
      if (a[propName] !== b[propName]) {
        return false;
      }
    }

    // If we made it this far, objects
    // are considered equivalent
    return true;
  };

  const showAndHideToast = async (props: ToastNotificationProps) => {
    showToast(props);
    await new Promise((resolve) => setTimeout(resolve, 5000));
    hideToast(props);
  };

  const showToast = (props: ToastNotificationProps) => {
    setToastProps(currentProps => currentProps.concat([props]));
  };

  const hideToast = (props: ToastNotificationProps) => {
    setToastProps(currentProps => currentProps.filter(prop => !arePropsEqual(prop, props)));
  };

  const sendLocalMessage = async (message: string) => {
    const response = await axios.post(`${window.location.origin}/kotlin-web/rest/jms/local/echo`, message);
    const result = response.data;
    const responseProps: ToastNotificationProps = {
      title: 'Response',
      subtitle: 'Local queue',
      caption: result[0],
      kind: 'success'
    };

    await showAndHideToast(responseProps);
  };

  const sendRemoteMessage = async (message: string) => {
    const response = await axios.post(`${window.location.origin}/kotlin-web/rest/jms/remote/send`, message);
    const result = response.data;
    const responseProps: ToastNotificationProps = {
      title: 'Status',
      subtitle: 'Remote queue',
      caption: result[0],
      kind: 'success'
    };

    await showAndHideToast(responseProps);
  };

  const receiveMessage = async () => {
    const response = await axios.get(`${window.location.origin}/kotlin-web/rest/jms/remote/receive`);
    let props: ToastNotificationProps;
    if (response.data.toString().indexOf('error') !== -1) {
      props = {
        title: 'Receive Failed',
        subtitle: 'Remote queue',
        caption: response.data,
        kind: 'error'
      }
    } else {
      props = {
        title: 'Response',
        subtitle: 'Remote queue',
        caption: response.data[0],
        kind: 'success'
      }
    }

    await showAndHideToast(props);
  };

  const produceMessageForBean = async (message: string) => {
    const response = await axios.post(`${window.location.origin}/kotlin-web/rest/jms/mdb/do_message`, message);
    const result = response.data;
    const responseProps: ToastNotificationProps = {
      title: 'Status',
      subtitle: 'Local message producer',
      caption: result[0],
      kind: 'success'
    };

    await showAndHideToast(responseProps)
  };

  const renderToast = () => {
      return (
        <div>
          {toastProps.map((prop) => (
            <ToastNotification {...prop} />
          ))}
        </div>
      )
  };

  return (
    <div className="bx--grid bx--grid--full-width landing-page">
      <div className="landing-page__toast">
        { renderToast() }
      </div>
      <div className="bx--row landing-page__banner">
        <div className="bx--col-lg-16">
          <Breadcrumb noTrailingSlash aria-label="Page navigation">
            <BreadcrumbItem>
              <a href="/">JMS Workbench</a>
            </BreadcrumbItem>
          </Breadcrumb>
          <h1 className="landing-page__heading">
            Test JMS Resources
          </h1>
        </div>
      </div>
      <div className="bx--row landing-page__r2">
        <div className="bx--col bx--no-gutter">
          <Tabs {...props.tabs} aria-label="Tab navigation">
            <Tab {...props.tab} label="Local Queue">
              <div className="bx--grid bx--grid--no-gutter bx--grid--full-width">
                <div className="bx--row landing-page__tab-content">
                  <div className="bx--col-md-4 bx--col-lg-7">
                    <h2 className="landing-page__subheading">
                      Local Queue Test
                    </h2>
                    <p className="landing-page__p">
                      This will send a message and return what was received.
                    </p>
                    <div className="landing-page__form">
                      <TextInput labelText={'Message to send'} id={'localTestText'} value={testMessage}
                                 onChange={(e) => setTestMessage(e.target.value)}/>
                      <Button onClick={() => sendLocalMessage(testMessage)} disabled={testMessage.length === 0}>Test local queue</Button>
                    </div>
                  </div>
                  <div className="bx--col-md-4 bx--offset-lg-1 bx--col-lg-8">
                    <img
                      className="landing-page__illo"
                      src={`${process.env.PUBLIC_URL}/tab-illo.png`}
                      alt="Carbon illustration"
                    />
                  </div>
                </div>
              </div>
            </Tab>
            <Tab {...props.tab} label="Remote Queue">
              <div className="bx--grid bx--grid--no-gutter bx--grid--full-width">
                <div className="bx--row landing-page__tab-content">
                  <div className="bx--col-md-4 bx--col-lg-7">
                    <h2 className="landing-page__subheading">
                      Remote Queue Test
                    </h2>
                    <p className="landing-page__p">
                      Send messages to the queue, then retrieve them. The queue is accessed remotely over the JMS endpoint.
                    </p>
                    <div className="landing-page__form">
                      <TextInput labelText={'Message to send'} id={'remoteTestText'} value={testMessage}
                                 onChange={(e) => setTestMessage(e.target.value)} />
                      <div className="landing-page__button-array">
                        <Button onClick={() => sendRemoteMessage(testMessage)} disabled={testMessage.length === 0}>Send message to remote queue</Button>
                        <Button onClick={() => receiveMessage()} >Attempt to receive message from remote queue</Button>
                      </div>

                    </div>
                  </div>
                  <div className="bx--col-md-4 bx--offset-lg-1 bx--col-lg-8">
                    <img
                      className="landing-page__illo"
                      src={`${process.env.PUBLIC_URL}/tab-illo.png`}
                      alt="Carbon illustration"
                    />
                  </div>
                </div>
              </div>
            </Tab>
            <Tab {...props.tab} label="MDBs">
              <div className="bx--grid bx--grid--no-gutter bx--grid--full-width">
                <div className="bx--row landing-page__tab-content">
                  <div className="bx--col-md-4 bx--col-lg-7">
                    <h2 className="landing-page__subheading">
                      Message-driven bean test
                    </h2>
                    <p className="landing-page__p">
                      This will send a message; an MDB should be activated and print a message to the server logs.
                    </p>
                    <div className="landing-page__form">
                      <TextInput labelText={'Message to send'} id={'mdbTestText'} value={testMessage}
                                 onChange={(e) => setTestMessage(e.target.value)}/>
                      <Button onClick={() => produceMessageForBean(testMessage)} disabled={testMessage.length === 0}>Create message for bean</Button>
                    </div>
                  </div>
                  <div className="bx--col-md-4 bx--offset-lg-1 bx--col-lg-8">
                    <img
                      className="landing-page__illo"
                      src={`${process.env.PUBLIC_URL}/tab-illo.png`}
                      alt="Carbon illustration"
                    />
                  </div>
                </div>
              </div>
            </Tab>
          </Tabs>
        </div>
      </div>
    </div>

  );
};

export default LandingPage;
