### Developer Notes

Information for developers to help them understand the design structure and workflow.

#### ServiceManager
The service manager is a static holder for widely used services throughout the SDK.
 
- **AppStateService**: Holds information on the AppStates available to run from the Project and whether or not they are
actively running.
 
 - **InspectorService**: Displays components that edit properties of the given object. Put simply, if you give it a Spatial
it will read the getters/setters and display various components (Vector3f, Enum, etc) that let you edit them. The Inspector
window is displayed on the right-hand side of the Main window.

- **JmeEngineService**: Gives access to the running JME application that is displayed to the user in the SDK.

- **ProjectInjectorService**: Injects the project into the SDK and registers any spatials, controls and filters that were found.

 - **SceneEditorService**: Provides the ability to load, display and save scenes (.j3o).

