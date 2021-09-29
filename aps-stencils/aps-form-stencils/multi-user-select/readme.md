#### FORM STENCILS for selecting multiple users from multi-select-dropdown in APS.

### Use case
* APS stencil should list all users in APS.
* APS users could select multiple values (users) from dropdown.

[Buy me a Coffee!](https://www.buymeacoffee.com/sherrymathews) 😉 ☕ 🍻 🎉 
[<br/><img alt="Buy me a coffee" width="250px" src="https://github.com/sherrymax/aps-examples/blob/master/bmc.png?raw=true" onclick="window.open('https://www.buymeacoffee.com/sherrymathews', '_blank');"/>]()


## Form runtime template
```html
<div ng-controller="MultiUserSelectController">
    <multiselect multiple="true" ng-model="selectedUsers"
        options="(c.firstName?c.firstName:'') + ' ' + (c.lastName?c.lastName:'') for c in masterUsersList"
        change="selected()">
    </multiselect>
    <br />

    <div ng-if="selectedUsers.length > 0">
        <b>Selected Users</b>
        <table>
            <tbody>
                <tr ng-repeat="s in selectedUsers">
                    <td style="padding-left:0.25em">
                        {{s.firstName?s.firstName:''}} {{s.lastName?s.lastName:''}} ({{s.email}})
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
</div>
```

# Custom Component Controller
<details>
  <summary>Click to expand!</summary>

``` typescript 
angular.module('activitiApp')
    .controller('MultiUserSelectController', ['$rootScope', '$scope', '$timeout', '$http',
        function ($rootScope, $scope, $timeout, $http) {
            console.log('SignModalCtrl instantiated');
            console.log($scope)
    
            $scope.selectedUsers = [];

            var fieldVal = $scope.field.value
            if(fieldVal !== null){
                $scope.selectedUsers = JSON.parse(fieldVal);
            }
            
            console.log("fieldVal >>>");
            console.log(fieldVal);
    
            $scope.name = 'World';
    
    
            $http.get('http://demo:demo@ec2-52-91-9-209.compute-1.amazonaws.com/activiti-app/api/enterprise/users').
            then(function (response) {
                console.log(response.data.data);
                //$scope.cars = Array.from(response.data.data);
                $scope.masterUsersList = Array.from(response.data.data);
            }).catch(function (response) {
                console.error(' error', response.status, response.data);
            });
    
            // Register this controller to listen to the form extensions methods
            $scope.registerCustomFieldListener(this);
            this.formRendered = function (form, scope) {
                console.log('>>> rendered')
                //$scope.accept(fieldVal)
            }
    
            // Setting the value before completing the task so it's properly stored
            this.formBeforeComplete = function (form, outcome, scope) {
                console.log('Before form complete');
                $scope.field.value = JSON.stringify($scope.selectedUsers);
                // $scope.field.value = $scope.selectedUsers;
            };
            
            $scope.uncheckItem = function ($index) {
                console.log('Going to delete item @ -->'+$index);
                $scope.selectedUsers.splice($index, 1);
                $scope.selectedUsers[$index].checked = false;
                //if($scope && $scope.items)
                //console.log('$scope.items.length -->'+$scope.items.length);
                //$scope.items[0].checked = false;
                //multiselect.items[0].checked = false;
                
                // angular.forEach($scope.selectedUsers, function (item) {
                    //item.checked = false;
                    //var index = $scope.selectedUsers.indexOf(item);
                // });
                //multiselect.sendMessage('Sending from controller ###');
            };
        }]);

//from bootstrap-ui typeahead parser
angular.module('activitiApp')
    .factory('optionParser', ['$parse', function ($parse) {

        console.log('*** Factory instantiated');


        //00000111000000000000022200000000000000003333333333333330000000000044000
        var TYPEAHEAD_REGEXP = /^\s*(.*?)(?:\s+as\s+(.*?))?\s+for\s+(?:([\$\w][\$\w\d]*))\s+in\s+(.*)$/;

        return {
            parse: function (input) {

                var match = input.match(TYPEAHEAD_REGEXP),
                    modelMapper, viewMapper, source;
                if (!match) {
                    throw new Error(
                        "Expected typeahead specification in form of '_modelValue_ (as _label_)? for _item_ in _collection_'" +
                        " but got '" + input + "'.");
                }

                return {
                    itemName: match[3],
                    source: $parse(match[4]),
                    viewMapper: $parse(match[2] || match[1]),
                    modelMapper: $parse(match[1])
                };
            }
        };
    }]);

angular.module('activitiApp').directive('multiselect', ['$parse', '$document', '$compile', 'optionParser',

    function ($parse, $document, $compile, optionParser) {

        console.log('*** multiselect Directive instantiated');

        return {
            restrict: 'E',
            require: 'ngModel',
            link: function (originalScope, element, attrs, modelCtrl) {

                var exp = attrs.options,
                    parsedResult = optionParser.parse(exp),
                    isMultiple = attrs.multiple ? true : false,
                    required = false,
                    scope = originalScope.$new(),
                    changeHandler = attrs.change || anguler.noop;

                scope.items = [];
                scope.header = 'Select';
                scope.multiple = isMultiple;
                scope.disabled = false;

                originalScope.$on('$destroy', function () {
                    scope.$destroy();
                });

                var popUpEl = angular.element('<multiselect-popup></multiselect-popup>');

                //required validator
                if (attrs.required || attrs.ngRequired) {
                    required = true;
                }
                attrs.$observe('required', function (newVal) {
                    required = newVal;
                });

                //watch disabled state
                scope.$watch(function () {
                    return $parse(attrs.disabled)(originalScope);
                }, function (newVal) {
                    scope.disabled = newVal;
                });

                //watch single/multiple state for dynamically change single to multiple
                scope.$watch(function () {
                    return $parse(attrs.multiple)(originalScope);
                }, function (newVal) {
                    isMultiple = newVal || false;
                });

                //watch option changes for options that are populated dynamically
                scope.$watch(function () {
                    return parsedResult.source(originalScope);
                }, function (newVal) {
                    if (angular.isDefined(newVal))
                        parseModel();
                });

                //watch model change
                scope.$watch(function () {
                    return modelCtrl.$modelValue;
                }, function (newVal, oldVal) {
                    //when directive initialize, newVal usually undefined. Also, if model value already set in the controller
                    //for preselected list then we need to mark checked in our scope item. But we don't want to do this every time
                    //model changes. We need to do this only if it is done outside directive scope, from controller, for example.
                    if (angular.isDefined(newVal)) {
                        markChecked(newVal);
                        scope.$eval(changeHandler);
                    }
                    getHeaderText();
                    modelCtrl.$setValidity('required', scope.valid());
                }, true);

                function parseModel() {
                    scope.items.length = 0;
                    var model = parsedResult.source(originalScope);
                    if (model) {
                        for (var i = 0; i < model.length; i++) {
                            var local = {};
                            local[parsedResult.itemName] = model[i];
                            scope.items.push({
                                label: parsedResult.viewMapper(local),
                                model: model[i],
                                checked: false
                            });
                        }
                    }

                }

                parseModel();

                element.append($compile(popUpEl)(scope));

                function getHeaderText() {
                    if (!modelCtrl.$modelValue || !modelCtrl.$modelValue.length) return scope.header = 'Select';
                    if (isMultiple) {
                        scope.header = modelCtrl.$modelValue.length + ' ' + 'selected';
                    } else {
                        var local = {};
                        local[parsedResult.itemName] = modelCtrl.$modelValue;
                        scope.header = parsedResult.viewMapper(local);
                    }
                }

                scope.valid = function validModel() {
                    if (!required) return true;
                    var value = modelCtrl.$modelValue;
                    return (angular.isArray(value) && value.length > 0) || (!angular.isArray(value) && value != null);
                };

                function selectSingle(item) {
                    if (item.checked) {
                        scope.uncheckAll();
                    } else {
                        scope.uncheckAll();
                        item.checked = !item.checked;
                    }
                    setModelValue(false);
                }
                
                function sendMessage(message){
                    console.log('*** ECHOING >>> '+message);
                }

                function selectMultiple(item) {
                    item.checked = !item.checked;
                    setModelValue(true);
                }

                function setModelValue(isMultiple) {
                    var value;

                    if (isMultiple) {
                        value = [];
                        angular.forEach(scope.items, function (item) {
                            if (item.checked) value.push(item.model);
                        })
                    } else {
                        angular.forEach(scope.items, function (item) {
                            if (item.checked) {
                                value = item.model;
                                return false;
                            }
                        })
                    }
                    modelCtrl.$setViewValue(value);
                }

                function markChecked(newVal) {
                    if (!angular.isArray(newVal)) {
                        angular.forEach(scope.items, function (item) {
                            if (angular.equals(item.model, newVal)) {
                                item.checked = true;
                                return false;
                            }
                        });
                    } else {
                        angular.forEach(newVal, function (i) {
                            angular.forEach(scope.items, function (item) {
                                if (angular.equals(item.model, i)) {
                                    item.checked = true;
                                }
                            });
                        });
                    }
                }

                scope.checkAll = function () {
                    if (!isMultiple) return;
                    angular.forEach(scope.items, function (item) {
                        item.checked = true;
                    });
                    setModelValue(true);
                };

                scope.uncheckAll = function () {
                    angular.forEach(scope.items, function (item) {
                        item.checked = false;
                    });
                    setModelValue(true);
                };

                scope.select = function (item) {
                    if (isMultiple === false) {
                        selectSingle(item);
                        scope.toggleSelect();
                    } else {
                        selectMultiple(item);
                    }
                }
                
                
            }
        };
    }
]);

</details>


angular.module('activitiApp').directive('multiselectPopup', ['$document', function ($document) {

    console.log('*** multiselectPopup Directive Instantiated ');
    return {
        restrict: 'E',
        scope: false,
        replace: true,
        template: '<div class="dropdown"><button class="btn-old" style="width: 100%; background-color: #36A7C4;" ng-click="toggleSelect()" ng-disabled="disabled" ng-class="{\'error\': !valid()}"><span class="pull-left">{{header}}</span><span class="caret pull-right"></span></button><ul class="dropdown-menu" style="width:100%"><li><input class="input-block-level" style="width:100%" type="text" ng-model="searchText.label" autofocus="autofocus" placeholder="Filter" /></li><li ng-show="multiple"><button class="btn-link btn-small" ng-click="checkAll()"><i class="glyphicon glyphicon-ok" style="color:GREEN"></i> Check all</button><button class="btn-link btn-small" ng-click="uncheckAll()"><i class="glyphicon glyphicon-remove" style="color:RED"></i> Uncheck all</button></li><li ng-repeat="i in items | filter:searchText"><a ng-click="select(i); focus()"><i ng-class="{\'glyphicon glyphicon-remove-circle text-danger\': i.checked, \'glyphicon glyphicon-ok-circle text-success\': !i.checked}" ></i>  {{" "+i.label}}</a></li></ul></div>',
        
        link: function (scope, element, attrs) {

            scope.isVisible = false;

            scope.toggleSelect = function () {
                if (element.hasClass('open')) {
                    element.removeClass('open');
                    $document.unbind('click', clickHandler);
                } else {
                    element.addClass('open');
                    scope.focus();
                    $document.bind('click', clickHandler);
                }
            };

            function clickHandler(event) {
                if (elementMatchesAnyInArray(event.target, element.find(event.target.tagName)))
                    return;
                element.removeClass('open');
                $document.unbind('click', clickHandler);
                scope.$digest();
            }

            scope.focus = function focus() {
                var searchBox = element.find('input')[0];
                searchBox.focus();
            }

            var elementMatchesAnyInArray = function (element, elementArray) {
                for (var i = 0; i < elementArray.length; i++)
                    if (element == elementArray[i])
                        return true;
                return false;
            }
        }
    }
}]);
```
</details>


## Runtime View
A runtime image of this stencil
![A runtime image of this stencil](assets/4.png)
![A runtime image of this stencil](assets/5.png)
![A runtime image of this stencil](assets/6.png)
![A runtime image of this stencil](assets/7.png)


## Stencil

* [A sample process app can be downloaded here.](assets/multi-select.zip)
* [The stencil can be downloaded here.](assets/multiuser-select-stencil.zip)

## References
1. https://docs.alfresco.com/process-services1.6/topics/custom_form_fields.html
2. https://docs.alfresco.com/process-services1.6/topics/example_3_dynamic_pie_chart.html